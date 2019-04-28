package cn.itruschina.crl.bean.dao;

import cn.itruschina.crl.bean.domain.AuthAndCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/17 13:30
 */

@Repository
public interface AuthAndCaDao extends JpaRepository<AuthAndCa, Long> {

    /***
     * 根据认证信息id和CA配置id查询中间关系
     * @param authorizationId 认证信息id
     * @param caConfigId CA配置id
     * @return
     */
    AuthAndCa findByAuthorizationIdAndCaConfigId(long authorizationId, long caConfigId);

    /***
     * 根据认证信息id和CA配置id删除中间关系
     * @param authorizationId 认证信息id
     * @param caConfigId CA配置id
     * @return
     */
    void deleteByAuthorizationIdAndCaConfigId(long authorizationId, long caConfigId);
}
