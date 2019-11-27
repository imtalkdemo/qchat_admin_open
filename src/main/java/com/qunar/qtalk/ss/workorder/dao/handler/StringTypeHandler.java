package com.qunar.qtalk.ss.workorder.dao.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@MappedJdbcTypes(JdbcType.ARRAY)
public class StringTypeHandler extends BaseTypeHandler<String[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    String[] parameter, JdbcType jdbcType) throws SQLException {


        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf("varchar", parameter);
        ps.setArray(i, array);
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName)
            throws SQLException {

        return getArray(rs.getArray(columnName));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {

        return getArray(rs.getArray(columnIndex));
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        return getArray(cs.getArray(columnIndex));
    }

    private String[] getArray(Array array) {

        if (array == null) {
            return null;
        }

        try {
            return (String[]) array.getArray();
        } catch (Exception e) {
           LOGGER.error("ArrayTypeHandler error", e);
        }

        return null;
    }
}