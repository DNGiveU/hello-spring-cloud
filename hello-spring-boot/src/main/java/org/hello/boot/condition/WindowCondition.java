package org.hello.boot.condition;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
public class WindowCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(Conditional.class.getName());
		System.out.println("==========@Conditional属性==========");
		for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
			System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
		}
		System.out.println("==========@Conditional属性==========EOF");
		Environment environment = context.getEnvironment();
		String osName = environment.getProperty("os.name");
		if (osName.contains("Window") || osName.contains("window")) {
			return true;
		}
		return false;
	}
}
