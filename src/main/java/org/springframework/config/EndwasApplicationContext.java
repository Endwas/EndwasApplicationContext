package org.springframework.config;

import org.springframework.annotation.*;
import org.springframework.exception.EndwasException;
import org.springframework.interfaces.BeanNameAware;
import org.springframework.interfaces.BeanPostProcessor;
import org.springframework.interfaces.InitializingBean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义容器
 *
 * @author endwas
 * @date Created in 2021/12/2 9:37
 */
public class EndwasApplicationContext {

    private static final String SINGLETON = "singleton";
    private Class<?> configClass;
    /**
     * 单例池, 简易版无三级缓存
     */
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * beanDefinitionMap
     */
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    /**
     * 简易版无beanFactoryPostProcessor
     */
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList();


    public EndwasApplicationContext(Class<?> configClass) {
        if (configClass == null) {
            throw new EndwasException("configClass cant null");
        }
        this.configClass = configClass;
        init();
    }

    private void init() {
        // 初始化 1、扫描 -> beanDefinitionMap 2、创建到singletonObjects
        String packageName = getPackagePath();
        // 扫描
        scan(packageName);
        // 创建bean
        createBean();
    }

    private void createBean() {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals(SINGLETON)) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        Object bean = null;
        try {
            // 1.实例bean
            bean = clazz.getDeclaredConstructor().newInstance();

            // 2.依赖注入
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object value = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(bean, value);
                }
            }

            // 3.Aware回调
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            // 如果是BeanPostProcessor则不调用before/after
            if (bean instanceof BeanPostProcessor) {
                initMethod(clazz, bean);
                return bean;
            }
            // 4.beanPostProcessor::postProcessBeforeInitialization
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            }
            initMethod(clazz, bean);

            // 6.beanPostProcessor::postProcessAfterInitialization aop动态代理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            }


        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;

    }

    private void initMethod(Class<?> clazz, Object bean) throws IllegalAccessException, InvocationTargetException {
        // 5.初始化
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // postConstruct
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                if (method.getParameterCount() != 0) {
                    throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
                }
                method.invoke(bean);
            }
        }
    }

    public Object getBean(String name) {
        Object bean;
        if (beanDefinitionMap.containsKey(name)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(name);
            if (beanDefinition.getScope().equals(SINGLETON)) {
                bean = singletonObjects.get(name);
            } else {
                bean = createBean(name, beanDefinition);
            }
        } else {
            throw new EndwasException("bean [" + name + "] is not exist");
        }
        return bean;
    }

    private String getPackagePath() {
        ComponentScan scanAnnotation = configClass.getDeclaredAnnotation(ComponentScan.class);
        String[] value = scanAnnotation.value();
        String packageName;
        // 没有设置componentScan使用config路径
        if (value.length == 0) {
            packageName = configClass.getPackageName();
        } else {
            packageName = value[0];
        }
        packageName = packageName.replace(".", "/");
        return packageName;
    }

    private void scan(String packageName) {
        ClassLoader classLoader = EndwasApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(packageName);
        assert resource != null;
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                String absolutePath = f.getPath();
                if (absolutePath.endsWith(".class")) {
                    String className = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    className = className.replace("\\", ".");
                    parse(classLoader, className);
                }
            }
        }
    }

    private void parse(ClassLoader classLoader, String path) {
        try {
            Class<?> clazz = classLoader.loadClass(path);
            // 判断是否是bean
            if (clazz.isAnnotationPresent(Component.class)) {
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                    beanPostProcessorList.add(instance);
                }
                Component component = clazz.getDeclaredAnnotation(Component.class);
                String beanName = component.value();
                if (beanName.isEmpty()) {
                    String simpleName = clazz.getSimpleName();
                    beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                }
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setClazz(clazz);
                if (clazz.isAnnotationPresent(Scope.class)) {
                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                    beanDefinition.setScope(scopeAnnotation.value());
                } else {
                    beanDefinition.setScope(SINGLETON);
                }

                beanDefinitionMap.put(beanName, beanDefinition);
            }

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
