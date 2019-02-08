package com.lunstudio.stocktechnicalanalysis.firebase;

import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FirebaseData {
	
	public static final String UP = "U";
	public static final String DOWN = "D";	
	
	@Override
	public String toString(){
		Object myself = this;
	    ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) {
            @Override
            protected boolean accept(Field field) {
                try {
                    return super.accept(field) && field.get(myself) != null;
                } catch (IllegalAccessException e) {
                    return super.accept(field);
                }
            }
	    };

	    return builder.toString();
		//return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE );
	}
}
