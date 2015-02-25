package com.sq.entity.pageAndSort;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 基础的查询排序对象.
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
public class Sort implements Iterable<OrderProperty>, Serializable {

    private static final long serialVersionUID = 5737186511678863905L;

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    /** 排序属性集合.*/
    private final List<OrderProperty> orders;

    // ==========================================
    // constructor...
    public Sort(OrderProperty... orders) {
        this(Arrays.asList(orders));
    }

    public Sort(List<OrderProperty> orders) {

        if (null == orders || orders.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
        }

        this.orders = orders;
    }

    public Sort(String... properties) {
        this(DEFAULT_DIRECTION, properties);
    }

    public Sort(Direction direction, String... properties) {
        this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
    }

    public Sort(Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<OrderProperty>(properties.size());

        for (String property : properties) {
            this.orders.add(new OrderProperty(direction, property));
        }
    }

    // ==========================================
    // 工具方法
    public Sort and(Sort sort) {

        if (sort == null) {
            return this;
        }

        ArrayList<OrderProperty> these = new ArrayList<OrderProperty>(this.orders);

        for (OrderProperty order : sort) {
            these.add(order);
        }

        return new Sort(these);
    }

    public OrderProperty getOrderFor(String property) {

        for (OrderProperty order : this) {
            if (order.getProperty().equals(property)) {
                return order;
            }
        }

        return null;
    }

    @Override
    public Iterator<OrderProperty> iterator() {
        return this.orders.iterator();
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

        if (!(obj instanceof Sort)) {
            return false;
        }

        Sort that = (Sort) obj;

        return this.orders.equals(that.orders);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + orders.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StringUtils.collectionToCommaDelimitedString(orders);
    }

}
