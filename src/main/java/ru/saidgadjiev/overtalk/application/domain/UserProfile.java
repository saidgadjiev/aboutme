package ru.saidgadjiev.overtalk.application.domain;

import ru.saidgadjiev.orm.next.core.field.*;
import ru.saidgadjiev.orm.next.core.table.DBTable;
import ru.saidgadjiev.orm.next.core.table.Unique;

import java.util.Set;

/**
 * Created by said on 18.03.2018.
 */
@DBTable(
        name = "user",
        uniqueConstraints = {
                @Unique(columns = {"userName"})
        }
)
public class UserProfile {

    @Getter(name = "getId")
    @Setter(name = "setId")
    @DBField(id = true, generated = true, dataType = DataType.INTEGER)
    private Integer id;

    @Getter(name = "getUserName")
    @Setter(name = "setUserName")
    @DBField(dataType = DataType.STRING, notNull = true)
    private String userName;

    @Getter(name = "getPassword")
    @Setter(name = "setPassword")
    @DBField(dataType = DataType.STRING)
    private String password;

    @Getter(name = "getUserRoles")
    @Setter(name = "setUserRoles")
    @ForeignCollectionField(foreignFieldName = "userProfile", fetchType = FetchType.LAZY)
    private Set<UserRoles> userRoles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserRoles> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoles> userRoles) {
        this.userRoles = userRoles;
    }
}