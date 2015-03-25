package com.sq.juint;

import com.sq.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:52
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 条件查询 自动生成
     *
     * @param username
     * @return
     */
    public User findByUsername(String username);


    /**
     * 关联查询 自动生成
     *
     * @param sex
     * @return
     */
    public User findByBaseInfoSex(Sex sex);

    public Page<User> findByBaseInfoSex(Sex sex, Pageable pageable);

    public List<User> findByBaseInfoSex(Sex sex, Sort sort);

}
