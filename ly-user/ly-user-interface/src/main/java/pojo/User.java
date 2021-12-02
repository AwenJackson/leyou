package pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class User implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "username")
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 4, max = 32, message = "用户长度名必须在4~32位")
    private String username;

    @Column(name = "password")
    @JsonIgnore     //输出json时，不输出password
    @Length(min = 4, max = 32, message = "密码长度必须在4~32位")
    private String password;

    @Column(name = "phone")
    @Pattern(regexp = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$", message = "手机号不正确")
    private String phone;

    @Column(name = "created")
    private Date created;

    @Column(name = "salt")
    @JsonIgnore
    private String salt;

    private static final long serialVersionUID = 1L;
}
