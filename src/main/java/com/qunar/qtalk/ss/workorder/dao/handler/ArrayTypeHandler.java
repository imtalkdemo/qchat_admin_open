package com.qunar.qtalk.ss.workorder.dao.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@MappedJdbcTypes(JdbcType.ARRAY)
public class ArrayTypeHandler extends BaseTypeHandler<Integer[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayTypeHandler.class);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Integer[] parameter, JdbcType jdbcType) throws SQLException {


        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf("integer", parameter);
        ps.setArray(i, array);
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, String columnName)
            throws SQLException {

        return getArray(rs.getArray(columnName));
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {

        return getArray(rs.getArray(columnIndex));
    }

    @Override
    public Integer[] getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        return getArray(cs.getArray(columnIndex));
    }

    private Integer[] getArray(Array array) {

        if (array == null) {
            return null;
        }

        try {
            return (Integer[]) array.getArray();
        } catch (Exception e) {
           LOGGER.error("ArrayTypeHandler error", e);
        }

        return null;
    }
}