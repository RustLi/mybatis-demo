package com.example.qy.mybatisdemo.interceptor;

import com.example.qy.mybatisdemo.annotations.*;
import com.example.qy.mybatisdemo.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.dao.DuplicateKeyException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @title MapF2FInterceptor
 * @desc TODO
 * @author shanyu
 * @date 2018年7月27日
 * @version 1.0
 */
@Intercepts(@Signature(method = "handleResultSets", type = ResultSetHandler.class, args = { Statement.class }))
public class MapF2Interceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MetaObject metaStatementHandler = ReflectUtil.getRealTarget(invocation);
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("mappedStatement");

        String className = StringUtils.substringBeforeLast(mappedStatement.getId(), ".");// 当前类
        String currentMethodName = StringUtils.substringAfterLast(mappedStatement.getId(), ".");// 当前方法
        Method currentMethod = findMethod(className, currentMethodName);// 获取当前Method

        if (currentMethod != null) {// 如果当前Method没有注解MapF2F
            if (currentMethod.getAnnotation(MapF2F.class) != null) {
                // 如果有MapF2F注解，则这里对结果进行拦截并转换
                MapF2F mapF2FAnnotation = currentMethod.getAnnotation(MapF2F.class);
                Statement statement = (Statement) invocation.getArgs()[0];
                Pair<Class<?>, Class<?>> kvTypePair = getKVTypeOfReturnMapF2F(currentMethod);// 获取返回Map里key-value的类型
                TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
                return result2MapF2F(statement, typeHandlerRegistry, kvTypePair, mapF2FAnnotation);
            } else if (currentMethod.getAnnotation(MapF2L.class) != null) {
                // 如果有MapF2L注解，则这里对结果进行拦截并转换
                MapF2L mapF2LAnnotation = currentMethod.getAnnotation(MapF2L.class);
                Statement statement = (Statement) invocation.getArgs()[0];
                Pair<Class<?>, Class<?>> kvTypePair = getKVTypeOfReturnMapF2L(currentMethod);// 获取返回Map里key-value的类型
                TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
                return result2MapF2L(statement, typeHandlerRegistry, kvTypePair, mapF2LAnnotation);
            } else if (currentMethod.getAnnotation(MapF2M.class) != null) {
                // 如果有MapF2M注解，则这里对结果进行拦截并转换
                MapF2M mapF2MAnnotation = currentMethod.getAnnotation(MapF2M.class);
                Statement statement = (Statement) invocation.getArgs()[0];
                Pair<Class<?>, Pair<Class<?>, Class<?>>> kvTypePair = getKVTypeOfReturnMapF2M(currentMethod);// 获取返回Map里key-value的类型
                TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
                return result2MapF2M(statement, typeHandlerRegistry, kvTypePair, mapF2MAnnotation);
            } else if (currentMethod.getAnnotation(MapF2ML.class) != null) {
                // 如果有MapF2M注解，则这里对结果进行拦截并转换
                MapF2ML mapF2MLAnnotation = currentMethod.getAnnotation(MapF2ML.class);
                Statement statement = (Statement) invocation.getArgs()[0];
                Pair<Class<?>, Pair<Class<?>, Class<?>>> kvTypePair = getKVTypeOfReturnMapF2ML(currentMethod);// 获取返回Map里key-value的类型
                TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
                return result2MapF2ML(statement, typeHandlerRegistry, kvTypePair, mapF2MLAnnotation);
            } else if (currentMethod.getAnnotation(MapF2A.class) != null) {
                // 如果有MapF2A注解，则这里对结果进行拦截并转换
                MapF2A mapF2AAnnotation = currentMethod.getAnnotation(MapF2A.class);
                Statement statement = (Statement) invocation.getArgs()[0];
                Pair<Class<?>, Class<?>> kvTypePair = getKVTypeOfReturnMapF2L(currentMethod);// 获取返回Map里key-value的类型
                TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();// 获取各种TypeHander的注册器
                return result2MapF2A(statement, typeHandlerRegistry, kvTypePair, mapF2AAnnotation);
            } else {
                return invocation.proceed();
            }
        } else {
            return invocation.proceed();
        }
    }

    @Override
    public Object plugin(Object obj) {
        return Plugin.wrap(obj, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 找到与指定函数名匹配的Method。
     *
     * @param className
     * @param targetMethodName
     * @return
     * @throws Throwable
     */
    private Method findMethod(String className, String targetMethodName) throws Throwable {
        Method[] methods = Class.forName(className).getDeclaredMethods();// 该类所有声明的方法
        if (methods == null) {
            return null;
        }

        for (Method method : methods) {
            if (StringUtils.equals(method.getName(), targetMethodName)) {
                return method;
            }
        }

        return null;
    }

    /**
     * 获取函数返回Map中key-value的类型
     * 
     * @param mapF2FMethod
     * @return left为key的类型，right为value的类型
     */
    private Pair<Class<?>, Class<?>> getKVTypeOfReturnMapF2F(Method mapF2FMethod) {
        Type returnType = mapF2FMethod.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (!Map.class.equals(parameterizedType.getRawType())) {
                throw new RuntimeException(
                    "[ERROR-MapF2F-return-map-type]使用MapF2F,返回类型必须是java.util.Map类型！！！method=" + mapF2FMethod);
            }
            return Pair.of((Class<?>) parameterizedType.getActualTypeArguments()[0],
                (Class<?>) parameterizedType.getActualTypeArguments()[1]);
        }

        return Pair.of(null, null);
    }

    /**
     * 获取函数返回Map中key-value的类型
     * 
     * @param mapF2LMethod
     * @return left为key的类型，right为value的类型
     */
    private Pair<Class<?>, Class<?>> getKVTypeOfReturnMapF2L(Method mapF2LMethod) {
        Type returnType = mapF2LMethod.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (!Map.class.equals(parameterizedType.getRawType())) {
                throw new RuntimeException(
                    "[ERROR-MapF2F-return-map-type]使用MapF2L,返回类型必须是java.util.Map<K,List<V>>类型！！！method="
                        + mapF2LMethod);
            }
            Type tmpType = parameterizedType.getActualTypeArguments()[1];
            if (tmpType instanceof ParameterizedType) {
                ParameterizedType tmpParameterizedType = (ParameterizedType) tmpType;
                if (!List.class.equals(tmpParameterizedType.getRawType())) {
                    throw new RuntimeException(
                        "[ERROR-MapF2L-return-map-type]使用MapF2M,返回类型必须是java.util.Map<K,List<V>>类型！！！method="
                            + mapF2LMethod);
                }
                return Pair.of((Class<?>) parameterizedType.getActualTypeArguments()[0],
                    (Class<?>) tmpParameterizedType.getActualTypeArguments()[0]);
            } else {
                return Pair.of(null, null);
            }
        }
        return Pair.of(null, null);
    }

    /**
     * 将查询结果映射成Map，其中第一个字段作为key，第二个字段作为value.
     * 
     * @param statement
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param kvTypePair 函数指定返回Map key-value的类型
     * @param mapF2FAnnotation
     * @return
     * @throws Throwable
     */
    private Object result2MapF2F(Statement statement, TypeHandlerRegistry typeHandlerRegistry,
        Pair<Class<?>, Class<?>> kvTypePair, MapF2F mapF2FAnnotation) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = Lists.newArrayList();
        Map<Object, Object> map = Maps.newHashMap();

        while (resultSet.next()) {
            Object key = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object value = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());

            if (map.containsKey(key)) {// 该key已存在
                if (!mapF2FAnnotation.isAllowKeyRepeat()) {// 判断是否允许key重复
                    throw new DuplicateKeyException("MapF2F duplicated key!key=" + key);
                }

                Object preValue = map.get(key);
                if (!mapF2FAnnotation.isAllowValueDifferentWithSameKey() && !Objects.equals(value, preValue)) {// 判断是否允许value不同
                    throw new DuplicateKeyException(
                        "MapF2F different value with same key!key=" + key + ",value1=" + preValue + ",value2=" + value);
                }
            }

            map.put(key, value);// 第一列作为key,第二列作为value。
        }

        res.add(map);
        return res;
    }

    /**
     * 获取函数返回Map中key-value的类型
     * 
     * @param mapF2MMethod
     * @return left为key的类型，right为value的类型
     */
    private Pair<Class<?>, Pair<Class<?>, Class<?>>> getKVTypeOfReturnMapF2M(Method mapF2MMethod) {
        Type returnType = mapF2MMethod.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (!Map.class.equals(parameterizedType.getRawType())) {
                throw new RuntimeException(
                    "[ERROR-MapF2L-return-map-type]使用MapF2M,返回类型必须是java.util.Map<K,Map<K,V>>类型！！！method="
                        + mapF2MMethod);
            }
            Type tmpType = parameterizedType.getActualTypeArguments()[1];
            if (tmpType instanceof ParameterizedType) {
                ParameterizedType tmpParameterizedType = (ParameterizedType) tmpType;
                if (!Map.class.equals(tmpParameterizedType.getRawType())) {
                    throw new RuntimeException(
                        "[ERROR-MapF2L-return-map-type]使用MapF2M,返回类型必须是java.util.Map<K,Map<K,V>>类型！！！method="
                            + mapF2MMethod);
                }
                return Pair.of((Class<?>) parameterizedType.getActualTypeArguments()[0],
                    Pair.of((Class<?>) tmpParameterizedType.getActualTypeArguments()[0],
                        (Class<?>) tmpParameterizedType.getActualTypeArguments()[1]));
            } else {
                return Pair.of(null, Pair.of(null, null));
            }
        }
        return Pair.of(null, Pair.of(null, null));
    }

    /**
     * 获取函数返回Map中key-value的类型
     * 
     * @param mapF2MMethod
     * @return left为key的类型，right为value的类型
     */
    private Pair<Class<?>, Pair<Class<?>, Class<?>>> getKVTypeOfReturnMapF2ML(Method mapF2MLMethod) {
        Type returnType = mapF2MLMethod.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (!Map.class.equals(parameterizedType.getRawType())) {
                throw new RuntimeException(
                    "[ERROR-MapF2ML-return-map-type]使用MapF2ML,返回类型必须是java.util.Map<K,Map<K,List<V>>>类型！！！method="
                        + mapF2MLMethod);
            }
            Type tmpType = parameterizedType.getActualTypeArguments()[1];
            if (tmpType instanceof ParameterizedType) {
                ParameterizedType tmpParameterizedType = (ParameterizedType) tmpType;
                if (!Map.class.equals(tmpParameterizedType.getRawType())) {
                    throw new RuntimeException(
                        "[ERROR-MapF2ML-return-map-type]使用MapF2ML,返回类型必须是java.util.Map<K,Map<K,List<V>>>类型！！！method="
                            + mapF2MLMethod);
                }
                Type tmp1Type = tmpParameterizedType.getActualTypeArguments()[1];
                if (tmp1Type instanceof ParameterizedType) {
                    ParameterizedType tmp1ParameterizedType = (ParameterizedType) tmp1Type;
                    if (!List.class.equals(tmp1ParameterizedType.getRawType())) {
                        throw new RuntimeException(
                            "[ERROR-MapF2ML-return-map-type]使用MapF2ML,返回类型必须是java.util.Map<K,Map<K,List<V>>>类型！！！method="
                                + mapF2MLMethod);
                    }
                    return Pair.of((Class<?>) parameterizedType.getActualTypeArguments()[0],
                        Pair.of((Class<?>) tmpParameterizedType.getActualTypeArguments()[0],
                            (Class<?>) tmp1ParameterizedType.getActualTypeArguments()[0]));
                }
            }
        }
        return Pair.of(null, Pair.of(null, null));
    }

    /**
     * 将查询结果映射成Map，其中第一个字段作为key，第二个字段作为value.
     * 
     * @param statement
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param kvTypePair 函数指定返回Map key-value的类型
     * @param mapF2FAnnotation
     * @return
     * @throws Throwable
     */
    private Object result2MapF2L(Statement statement, TypeHandlerRegistry typeHandlerRegistry,
        Pair<Class<?>, Class<?>> kvTypePair, MapF2L mapF2FAnnotation) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = Lists.newArrayList();
        Map<Object, List<Object>> map = Maps.newHashMap();

        while (resultSet.next()) {
            Object key = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object value = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            List<Object> tmpList = map.getOrDefault(key, Lists.newArrayList());
            tmpList.add(value);
            map.put(key, tmpList);// 第一列作为key,第二列作为value。
        }

        res.add(map);
        return res;
    }

    /**
     * 将查询结果映射成Map，其中第一个字段作为key，第二个字段作为value.
     * 
     * @param statement
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param kvTypePair 函数指定返回Map key-value的类型
     * @param mapF2FAnnotation
     * @return
     * @throws Throwable
     */
    private Object result2MapF2M(Statement statement, TypeHandlerRegistry typeHandlerRegistry,
        Pair<Class<?>, Pair<Class<?>, Class<?>>> kvTypePair, MapF2M mapF2FAnnotation) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = Lists.newArrayList();
        Map<Object, Map<Object, Object>> map = Maps.newHashMap();

        while (resultSet.next()) {
            Object colum1 = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object colum2 = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue().getKey());
            Object colum3 = this.getObject(resultSet, 3, typeHandlerRegistry, kvTypePair.getValue().getValue());

            Map<Object, Object> tmpMap = map.getOrDefault(colum1, Maps.newHashMap());
            map.put(colum1, tmpMap);
            tmpMap.put(colum2, colum3);
        }
        res.add(map);
        return res;
    }

    /**
     * 将查询结果映射成Map，其中第一个字段作为key，第二个字段作为value.
     * 
     * @param statement
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param kvTypePair 函数指定返回Map key-value的类型
     * @param mapF2FAnnotation
     * @return
     * @throws Throwable
     */
    private Object result2MapF2ML(Statement statement, TypeHandlerRegistry typeHandlerRegistry,
        Pair<Class<?>, Pair<Class<?>, Class<?>>> kvTypePair, MapF2ML mapF2MLAnnotation) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = Lists.newArrayList();
        Map<Object, Map<Object, List<Object>>> map = Maps.newHashMap();

        while (resultSet.next()) {
            Object colum1 = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object colum2 = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue().getKey());
            Object colum3 = this.getObject(resultSet, 3, typeHandlerRegistry, kvTypePair.getValue().getValue());

            Map<Object, List<Object>> tmpMap = map.getOrDefault(colum1, Maps.newHashMap());
            List<Object> tmpList = tmpMap.getOrDefault(colum2, Lists.newArrayList());
            tmpList.add(colum3);
            map.put(colum1, tmpMap);
            tmpMap.put(colum2, tmpList);
        }
        res.add(map);
        return res;
    }

    /**
     * 结果类型转换。
     * <p>
     * 这里借用注册在MyBatis的typeHander（包括自定义的），方便进行类型转换。
     * 
     * @param resultSet
     * @param columnIndex 字段下标，从1开始
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param javaType 要转换的Java类型
     * @return
     * @throws SQLException
     */
    private Object getObject(ResultSet resultSet, int columnIndex, TypeHandlerRegistry typeHandlerRegistry,
        Class<?> javaType) throws SQLException {
        final TypeHandler<?> typeHandler = typeHandlerRegistry.hasTypeHandler(javaType)
            ? typeHandlerRegistry.getTypeHandler(javaType) : typeHandlerRegistry.getUnknownTypeHandler();

        return typeHandler.getResult(resultSet, columnIndex);

    }

    private Object result2MapF2A(Statement statement, TypeHandlerRegistry typeHandlerRegistry,
        Pair<Class<?>, Class<?>> kvTypePair, MapF2A mapF2AAnnotation) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        List<Object> res = Lists.newArrayList();
        Map<Object, List<Object>> map = Maps.newHashMap();

        while (resultSet.next()) {
            Object key = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object value1 = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            Object value2 = this.getObject(resultSet, 3, typeHandlerRegistry, kvTypePair.getValue());
            List<Object> tmpList = map.getOrDefault(key, Lists.newArrayList());
            tmpList.add(value1);
            tmpList.add(value2);
            map.put(key, tmpList);// 第一列作为key,第二列,第三列放入List作为value。
        }
        res.add(map);
        return res;
    }
}
