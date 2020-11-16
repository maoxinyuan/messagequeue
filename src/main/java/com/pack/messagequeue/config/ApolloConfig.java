package com.pacific.messagequeue.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

/**
 * 封装读取apollo配置注入到spring容器中
 * @author maoxy
 */
@Component
public class ApolloConfig implements EnvironmentAware, BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
    @Override
    public void setEnvironment(Environment environment) {
            Config config = ConfigService.getAppConfig();
            Set<String> propertyNames = config.getPropertyNames();
            Iterator<String> it  = propertyNames.iterator();
            while (it.hasNext()){
                String next = it.next();
                Object  value = config.getProperty(next,null);
                ((ConfigurableEnvironment) environment).getPropertySources()
                        // 这里是 addFirst,优先级高于 application.properties 配置
                        .addFirst(new PropertySource<Object>(next, value) {
                            @Override
                            public Object getProperty(String s) {
                                if (s.equals(next)) {
                                    // 返回构造方法中的 source
                                    return source;
                                }
                                return null;
                            }
                        });
            }
    }
}
