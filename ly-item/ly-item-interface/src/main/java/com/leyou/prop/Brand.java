package com.leyou.prop;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Table(name = "tb_brand")
public class Brand implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "letter")
    private String letter;

    private static final long serialVersionUID = 1L;
}