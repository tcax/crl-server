package cn.itruschina.crl.bean.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/16 11:23
 */

@Entity
@Table(name = "crl_record")
@Data
public class CrlRecord extends BaseEntity {

    private long caConfigId;
    private String serialNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date revocationDate;
    private String revocationReason;

    public CrlRecord(long caConfigId, String serialNumber, Date revocationDate, String revocationReason) {
        this.caConfigId = caConfigId;
        this.serialNumber = serialNumber;
        this.revocationDate = revocationDate;
        this.revocationReason = revocationReason;
    }

    public CrlRecord() {
    }
}
