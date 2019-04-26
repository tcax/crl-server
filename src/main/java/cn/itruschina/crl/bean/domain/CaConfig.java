package cn.itruschina.crl.bean.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 11:22
 */

@Entity
@Table(name = "ca_config")
@Data
public class CaConfig extends BaseEntity {

    private String name;
    @Column(unique = true)
    private String baseCrlUrl;
    @Column(unique = true)
    private String deltaCrlUrl;
    @Column(length = 2048)
    private String base64CertString;
    private String subjectDn;
    private int retryTime = 0;
    @JsonIgnore
    private Date deltaThisUpdate;
    @JsonIgnore
    private Date deltaNextUpdate;

}
