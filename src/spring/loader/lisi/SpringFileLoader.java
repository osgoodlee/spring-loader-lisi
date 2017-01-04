package spring.loader.lisi;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringFileLoader {

	public static boolean loadJarFile(String jarFileUrl, XmlWebApplicationContext context) {
		try {
			String packageName = "moduletest";
			//如果无法成功加载jar包，退出
			if (!loadToClassLoader(jarFileUrl)) {
				return false;
			}
			// 注册注解方式的bean
			//获取web应用正使用的bean容器，jar包中的bean将被注入到容器中
			DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context
					.getAutowireCapableBeanFactory();
			//创建Spring的注解自动扫描器(构造函数传入容器，扫描后的bean注入该容器)，用来获取基于注解的bean，扫描器需要指定要扫描的包名，扫描jar中的包路径，使用通配符，
			//另外在导出jar时必须选择add directory entries（即把目录也加入到jar中），否则spring扫描时将无法找到class
			ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(defaultListableBeanFactory);
//			System.out.println(defaultListableBeanFactory.getBeanDefinitionCount());
			//开始扫描
			scanner.scan(packageName);
//			System.out.println(defaultListableBeanFactory.getBeanDefinitionCount());
			
			// 注册xml配置中的bean到根上下文
			String configurationFilePath = "jar:file:/" + jarFileUrl + "!/applicationContext.xml";
			URL url = new URL(configurationFilePath);
			UrlResource urlResource = new UrlResource(url);
//			DefaultListableBeanFactory xmlBeanFactory = new XmlBeanFactory(urlResource);
//			String[] beanIds = xmlBeanFactory.getBeanDefinitionNames();
//			for (String beanId : beanIds) {
//				BeanDefinition bd = xmlBeanFactory.getMergedBeanDefinition(beanId);
//				defaultListableBeanFactory.registerBeanDefinition(beanId, bd);
//			}
			//创建一个DefaultListableBeanFactory容器，用于获取applicationContext.xml文件中配置的bean
			DefaultListableBeanFactory xmlBeanFactory = new DefaultListableBeanFactory();
			//用该DefaultListableBeanFactory容器创建一个XmlBeanDefinitionReader，用于加载bean配置
			XmlBeanDefinitionReader xmlBeanReader = new XmlBeanDefinitionReader(xmlBeanFactory);
			//加载applicationContext.xml文件中配置的bean
			xmlBeanReader.loadBeanDefinitions(urlResource);
			//获取该容器中所有bean的名称
			String[] beanIds = xmlBeanFactory.getBeanDefinitionNames();
			//遍历这些新的bean，加入到defaultListableBeanFactory应用容器中
			for (String beanId : beanIds) {
				BeanDefinition bd = xmlBeanFactory.getMergedBeanDefinition(beanId);
				defaultListableBeanFactory.registerBeanDefinition(beanId, bd);
			}
			
			//获取SpringMVC中的HandlerMapping处理器，它也是一个bean．HandlerMapping将会把请求映射为HandlerExecutionChain对象
			//（包含一个Handler处理器（页面控制器）对象、多个HandlerInterceptor拦截器）对象
			ManualAnnotationHandlerMapping handlerMapping = context.getBean(ManualAnnotationHandlerMapping.class);
			//如果handlerMapping不为空，表明是SpringMVC项目，处理新加jar包中的映射关系到handlerMapping中
			if (null != handlerMapping) {
				String[] beansName = defaultListableBeanFactory.getBeanDefinitionNames();
				//遍历容器中所有bean
				for (String beanName : beansName) {
					//获取bean对应的映射url
					String[] mappedURLs = handlerMapping.determineUrlsForHandlerByManual(beanName);
					//映射url不为空，表明该bean是controller类中的响应方法，把该bean和对应映射url的关系写入handlerMapping
					if (!ObjectUtils.isEmpty(mappedURLs)) {
						handlerMapping.registerHandlerByManual(mappedURLs, beanName);
					}
				}
			}
			System.out.println(defaultListableBeanFactory.getBeanDefinitionCount());
			// 手动拿jar包里的bean的实例时因为存在ClassLoader的隔离虽然在开始设置了ContextClassLoader
			// 但是Spring默认getBean的时候并没有每次都去拿最新的ContextClassLoader使用，所以需要手动设置Bean的ClassLoader
			// 因为是手动设置的这里存在线程安全的问题...不知道有没有其他方法.
			// DefaultListableBeanFactory factory =
			// (DefaultListableBeanFactory)context.getAutowireCapableBeanFactory();
			// factory.setBeanClassLoader(loader);
			
			
//			System.out.println(context.getBean("collectController"));
//			System.out.println(context.getBean("house"));

			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 将jar包加载到类加载器
	 * @param jarFileUrl
	 * @throws MalformedURLException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static boolean loadToClassLoader(String jarFileUrl) throws MalformedURLException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		File jarFile = new File(jarFileUrl);
		URL jarUrl = jarFile.toURI().toURL();
		// 对于jar文件，可以理解为一个存放class文件的文件夹
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		boolean accessible = method.isAccessible(); // 获取方法的访问权限
		try {
			if (accessible == false) {
				method.setAccessible(true); // 设置方法的访问权限
			}
			// 获取当前线程类加载器
			URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			method.invoke(classLoader, jarUrl);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			method.setAccessible(accessible);
		}
	}
}
