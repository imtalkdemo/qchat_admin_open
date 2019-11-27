package com.qunar.qtalk.ss.workorder.dao.handler;


import com.qunar.qtalk.ss.utils.JacksonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({Object.class})
public class JsonTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws
            SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(JacksonUtils.obj2String(o));
        preparedStatement.setObject(i, jsonObject);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Object object = resultSet.getObject(s);
        if (object instanceof PGobject) {
            PGobject pGobject = (PGobject) object;
            return pGobject.getValue();
        }
        return object;
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Object object = resultSet.getObject(i);
        if (object instanceof PGobject) {
            PGobject pGobject = (PGobject) object;
            return pGobject.getValue();
        }
        return object;
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Object object = callableStatement.getObject(i);
        if (object instanceof PGobject) {
            PGobject pGobject = (PGobject) object;
            return pGobject.getValue();
        }
        return object;
    }
}
