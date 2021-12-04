# EndwasApplicationContext

Learning Spring and writer a small demo 

including
- EndwasApplicationContext
- BeanPostProcessor
- Autowire
- PostConstruct
- ComponentScan
- BeanDefinition
- InitializingBean
etc..

next step
1. ComponentScan can recursively query path and sub path..

bug
1. Component需要增加运行时作用。
2. classLoader loadclass需要路径是 com.xxx.xxx.class
3. classLoader getResource需要的是 com/xxx/xxx路径
