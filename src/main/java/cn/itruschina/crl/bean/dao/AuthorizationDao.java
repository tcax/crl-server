package cn.itruschina.crl.bean.dao;

import cn.itruschina.crl.bean.domain.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:30
 */

@Repository
public interface AuthorizationDao extends JpaRepository<Authorization, Long> {

    /***
     * 根据token获取认证信息
     * @param accessToken
     * @return
     */
    Authorization findByAccessToken(String accessToken);

}
