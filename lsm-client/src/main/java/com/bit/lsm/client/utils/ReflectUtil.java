/**
 * Copyright (c) 2014, wylipengming@jd.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.utils;

import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.utils <br>
 * <b>类名称</b>： ReflectUtil <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:wylipengming@jd.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/28 14:49<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class ReflectUtil {
    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<String, Field>();

    /**
     * get parameter desc
     *
     * @param parameterTypes
     * @return
     */
    public static String getMethodParametersDesc(String methodName, CtClass[] parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName);
        sb.append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            boolean first = true;
            for (CtClass type : parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                try {
                    sb.append(desc2name(getDesc(type)));
                } catch (NotFoundException e) {
                    //ignore
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * get parameter desc
     *
     * @param parameterTypes
     * @return
     */
    public static String getMethodParametersDesc(String methodName, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName);
        sb.append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            boolean first = true;
            for (Class<?> type : parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(desc2name(getDesc(type)));
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * get class desc.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param c class.
     * @return desc.
     * @throws NotFoundException
     */
    public static String getDesc(Class<?> c)
    {
        StringBuilder ret = new StringBuilder();

        while( c.isArray() )
        {
            ret.append('[');
            c = c.getComponentType();
        }

        if( c.isPrimitive() )
        {
            String t = c.getName();
            if( "void".equals(t) ) ret.append(JVM_VOID);
            else if( "boolean".equals(t) ) ret.append(JVM_BOOLEAN);
            else if( "byte".equals(t) ) ret.append(JVM_BYTE);
            else if( "char".equals(t) ) ret.append(JVM_CHAR);
            else if( "double".equals(t) ) ret.append(JVM_DOUBLE);
            else if( "float".equals(t) ) ret.append(JVM_FLOAT);
            else if( "int".equals(t) ) ret.append(JVM_INT);
            else if( "long".equals(t) ) ret.append(JVM_LONG);
            else if( "short".equals(t) ) ret.append(JVM_SHORT);
        }
        else
        {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    /**
     * get class desc.
     * Object.class => "Ljava/lang/Object;"
     * boolean[].class => "[Z"
     *
     * @param c class.
     * @return desc.
     * @throws javassist.NotFoundException
     */
    public static String getDesc(final CtClass c) throws NotFoundException
    {
        StringBuilder ret = new StringBuilder();
        if( c.isArray() )
        {
            ret.append('[');
            ret.append(getDesc(c.getComponentType()));
        }
        else if( c.isPrimitive() )
        {
            String t = c.getName();
            if( "void".equals(t) ) ret.append(JVM_VOID);
            else if( "boolean".equals(t) ) ret.append(JVM_BOOLEAN);
            else if( "byte".equals(t) ) ret.append(JVM_BYTE);
            else if( "char".equals(t) ) ret.append(JVM_CHAR);
            else if( "double".equals(t) ) ret.append(JVM_DOUBLE);
            else if( "float".equals(t) ) ret.append(JVM_FLOAT);
            else if( "int".equals(t) ) ret.append(JVM_INT);
            else if( "long".equals(t) ) ret.append(JVM_LONG);
            else if( "short".equals(t) ) ret.append(JVM_SHORT);
        }
        else
        {
            ret.append('L');
            ret.append(c.getName().replace('.','/'));
            ret.append(';');
        }
        return ret.toString();
    }

    /**
     * desc to name.
     * "[[I" => "int[][]"
     *
     * @param desc desc.
     * @return name.
     */
    public static String desc2name(String desc) {
        StringBuilder sb = new StringBuilder();
        int c = desc.lastIndexOf('[') + 1;
        if (desc.length() == c + 1) {
            switch (desc.charAt(c)) {
                case JVM_VOID: {
                    sb.append("void");
                    break;
                }
                case JVM_BOOLEAN: {
                    sb.append("boolean");
                    break;
                }
                case JVM_BYTE: {
                    sb.append("byte");
                    break;
                }
                case JVM_CHAR: {
                    sb.append("char");
                    break;
                }
                case JVM_DOUBLE: {
                    sb.append("double");
                    break;
                }
                case JVM_FLOAT: {
                    sb.append("float");
                    break;
                }
                case JVM_INT: {
                    sb.append("int");
                    break;
                }
                case JVM_LONG: {
                    sb.append("long");
                    break;
                }
                case JVM_SHORT: {
                    sb.append("short");
                    break;
                }
                default:
                    throw new RuntimeException();
            }
        } else {
            sb.append(desc.substring(c + 1, desc.length() - 1).replace('/', '.'));
        }
        while (c-- > 0) sb.append("[]");
        return sb.toString();
    }

    /**
     * 通用 根据表达式获取对象属性值
     *
     * @param o
     * @param fPath 类似xpath表达式： b/c
     * @return
     */
    public static Object getValue(Object o, String fPath) throws Exception {
        if (o == null || fPath == null || "".equals(fPath)) {
            return null;
        }
        Class c = o.getClass();
        Object mid = o;
        String[] path = fPath.split("/");
        for (String s : path) {
            if (isPrimitiveClass(s)) {
                return mid;
            }
            mid = getField(c, mid, s);
            if (mid == null) {
                break;
            }
            c = mid.getClass();
        }
        return mid;
    }

    private static boolean isPrimitiveClass(String name) {

        return name != null &&
                (Boolean.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Integer.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Long.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Double.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Character.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Byte.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Short.TYPE.getSimpleName().equalsIgnoreCase(name)
                        || Float.TYPE.getSimpleName().equalsIgnoreCase(name))
                ||"String".equalsIgnoreCase(name);
    }

    private static Object getField(Class s, Object o, String name) throws Exception {
        Field field = FIELD_CACHE.get(s.getCanonicalName() + "." + name);
        if (field == null) {
            field = s.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            FIELD_CACHE.put(s.getCanonicalName() + "." + name, field);
        }
        return field.get(o);
    }

    private ReflectUtil() {
    }
}
