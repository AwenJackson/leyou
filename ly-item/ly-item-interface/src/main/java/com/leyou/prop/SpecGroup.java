package com.leyou.prop;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Table(name = "tb_spec_group")
public class SpecGroup implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "cid")
    private Long cid;

    @Column(name = "name")
    private String name;

    @Transient
    private List<SpecParam> params;

    private static final long serialVersionUID = 1L;
}