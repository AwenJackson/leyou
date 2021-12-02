package com.leyou.prop;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Table(name = "tb_spu")
public class Spu implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "cid1")
    private Long cid1;

    @Column(name = "cid2")
    private Long cid2;

    @Column(name = "cid3")
    private Long cid3;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "saleable")
    private Boolean saleable;

    @Column(name = "valid")
    private Boolean valid;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    @Transient
    private String cname;

    @Transient
    private String bname;

    @Transient
    private List<Sku> skus;

    @Transient
    private Spudetail spuDetail;


    private static final long serialVersionUID = 1L;
}