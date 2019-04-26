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

}
