package xyz.dreature.smit.common.handler;

import net.postgis.jdbc.jts.JtsGeometry;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({String.class})
public class GeoTypeHandler extends BaseTypeHandler<String> {

    private static final int SRID = 4326; // WGS84

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            Geometry geometry = new WKTReader().read(parameter);
            geometry.setSRID(SRID);
            ps.setObject(i, new JtsGeometry(geometry));
        } catch (Exception e) {
            throw new SQLException("Invalid WKT string: " + parameter, e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToWkt(rs.getObject(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToWkt(rs.getObject(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToWkt(cs.getObject(columnIndex));
    }

    private String convertToWkt(Object value) throws SQLException {
        if (value == null) {
            return null;
        }

        if (value instanceof JtsGeometry) {
            return new WKTWriter().write(((JtsGeometry) value).getGeometry());
        }

        if (value instanceof Geometry) {
            return new WKTWriter().write((Geometry) value);
        }

        if (value instanceof PGobject) {
            String pgValue = ((PGobject) value).getValue();
            if (pgValue != null && !pgValue.isEmpty()) {
                return pgValue;
            }
        }

        return value.toString();
    }
}