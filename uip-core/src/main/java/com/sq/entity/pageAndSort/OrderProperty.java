package com.sq.entity.pageAndSort;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 排序属性对象.为Sort提供支持.
 * User: shuiqing
 * Date: 2015/2/25
 * Time: 20:27
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class OrderProperty implements Serializable {

    private static final long serialVersionUID = 1522511010900108987L;

    // ==========================================
    // fields...
    /** 默认忽略大小写.*/
    private static final boolean DEFAULT_IGNORE_CASE = false;

    /** 默认正序排序.*/
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    /** 排序方向.*/
    private final Direction direction;

    /** 排序属性.*/
    private final String property;

    private final boolean ignoreCase;

    // ==========================================
    // constructor...
    public OrderProperty(Direction direction, String property) {

        this(direction, property, DEFAULT_IGNORE_CASE);
    }

    public OrderProperty(String property) {
        this(DEFAULT_DIRECTION, property);
    }

    private OrderProperty(Direction direction, String property, boolean ignoreCase) {

        if (!StringUtils.hasText(property)) {
            throw new IllegalArgumentException("Property must not null or empty!");
        }

        this.direction = direction == null ? DEFAULT_DIRECTION : direction;
        this.property = property;
        this.ignoreCase = ignoreCase;
    }

    @Deprecated
    public static List<OrderProperty> create(Direction direction, Iterable<String> properties) {

        List<OrderProperty> orders = new ArrayList<OrderProperty>();
        for (String property : properties) {
            orders.add(new OrderProperty(direction, property));
        }
        return orders;
    }

    // ==========================================
    // 工具方法
    public Direction getDirection() {
        return direction;
    }

    public String getProperty() {
        return property;
    }

    public boolean isAscending() {
        return this.direction.equals(Direction.ASC);
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public OrderProperty with(Direction order) {
        return new OrderProperty(order, this.property);
    }

    public Sort withProperties(String... properties) {
        return new Sort(this.direction, properties);
    }

    public OrderProperty ignoreCase() {
        return new OrderProperty(direction, property, true);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + direction.hashCode();
        result = 31 * result + property.hashCode();

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OrderProperty)) {
            return false;
        }

        OrderProperty that = (OrderProperty) obj;

        return this.direction.equals(that.direction) && this.property.equals(that.property);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s: %s", property, direction);
    }
}
