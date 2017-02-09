package spring.loader.lisi;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.UrlResource;

public class Main {

	@SuppressWarnings({ "resource" })
	public static void main(String[] args){  
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/spring1.xml");  
//        PluginLoader testLoader = (PluginLoader) context.getBean("pluginLoader");
//        testLoader.load();
        
        
        try {  
        	//保存当前类加载器
        	ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        	System.out.println("当前线程CL:"+currentClassLoader.toString());
        	
        	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(  
            		"classpath*:/spring1.xml");  
        	
//        	   Object sdf = applicationContext.getBean("hibernateSqlDao");  
//               tryInvoke(sdf);  
//               System.out.println(sdf.getClass().toString());
//               
//               CollectService dfdf = (CollectService) applicationContext.getBean("collectService");  
//               dfdf.uploadTransactionReport();
//               System.out.println(dfdf.getClass().toString());
            
//            PluginLoader testLoader = (PluginLoader) applicationContext.getBean("pluginLoader");
//            testLoader.load();
//            CollectService pluginBean2 = (CollectService) applicationContext.getBean("collectService");  
//   		 	pluginBean2.uploadTransactionReport();
//            System.out.println(pluginBean2.getClass().toString());
            
//            HibernateSqlDao pluginBean3 = (HibernateSqlDao) applicationContext.getBean("hibernateSqlDao");  
//            pluginBean3.getSessionFactory();
//            System.out.println(pluginBean3.getClass().toString());
           
  
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext  
                    .getAutowireCapableBeanFactory();  
            
            // 以下这行设置BeanFactory的ClassLoader，以加载外部类  
            setBeanClassLoader(beanFactory);  
   
//            String configurationFilePath = "jar:file:/E:/ncr-connectors-dibs-tms-service-dibs.jar!/module.xml";  
//            URL url = new URL(configurationFilePath);  
//            UrlResource urlResource = new UrlResource(url);  
//            DefaultListableBeanFactory xbf = new XmlBeanFactory(urlResource);  
//            String[] beanIds = xbf.getBeanDefinitionNames();  
//            for (String beanId : beanIds) {  
//                BeanDefinition bd = xbf.getMergedBeanDefinition(beanId);  
////                System.out.println(bd.getBeanClassName());
//                beanFactory.registerBeanDefinition(beanId, bd);  
//            }  
            
            System.out.println("当前线程CL:"+Thread.currentThread().getContextClassLoader().toString());
            System.out.println("当前上下文CL:"+applicationContext.getClassLoader().toString());
            
//            beanFactory.setBeanClassLoader(currentClassLoader);  
//            for (String beanId : applicationContext.getBeanDefinitionNames()) {  
//                System.out.println(beanId);
//            }  
            System.out.println("当前上下文CL:"+applicationContext.getClassLoader().toString());
            System.out.println("当前工厂CL:"+((DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory()).getBeanClassLoader().toString());

            
//            Object hibernateSqlDao = applicationContext.getBean("hibernateSqlDao");  
//            tryInvoke(hibernateSqlDao);  
//            System.out.println(hibernateSqlDao.getClass().toString());
//            
//            beanFactory.setBeanClassLoader(currentClassLoader);
//            System.out.println("当前工厂CL:"+((DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory()).getBeanClassLoader().toString());
//
//            Object t2 = applicationContext.getBean("hibernateSqlDao");  
//            tryInvoke(t2);  
//            System.out.println(t2.getClass().toString());
            
            Object t33 = applicationContext.getBean("collectService");  
            tryInvokeCollect(t33);  
            System.out.println(t33.getClass().toString());
          
  
        } catch (Exception exc) {  
            exc.printStackTrace();  
        }  
    }  
	
    private static void setBeanClassLoader(  
            DefaultListableBeanFactory beanFactory)  
            throws MalformedURLException {  
        String jarFilePath = "E://ncr-connectors-dibs-tms-service-dibs.jar";  
        URL jarUrl = new File(jarFilePath).toURI().toURL();  
        URL[] urls = new URL[] { jarUrl };  
        URLClassLoader cl = new URLClassLoader(urls);  
        
        Thread.currentThread().setContextClassLoader(cl.getParent().getParent());
        beanFactory.setBeanClassLoader(cl.getParent().getParent());  
        
    }  
  
    private static void tryInvoke(Object bean) throws SecurityException,  
            NoSuchMethodException, IllegalArgumentException,  
            IllegalAccessException, InvocationTargetException {  
  
        Class<?> paramTypes[] = new Class[0];  
        Method method = bean.getClass()  
                .getDeclaredMethod("getSessionFactory", paramTypes);  
        Object paramValues[] = new Object[0];  
        method.invoke(bean, paramValues);  
  
    }  
    
	private static void tryInvokeCollect(Object bean) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Class<?> paramTypes[] = new Class[0];
		Method method = bean.getClass().getDeclaredMethod("uploadTransactionReport",
				paramTypes);
		Object paramValues[] = new Object[0];
		method.invoke(bean, paramValues);

	}
    
}
