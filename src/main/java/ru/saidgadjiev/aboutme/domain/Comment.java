package ru.saidgadjiev.aboutme.domain;

import ru.saidgadjiev.aboutme.dao.SerialTypeDataPersister;
import ru.saidgadjiev.aboutme.dao.TextTypeDataPersister;
import ru.saidgadjiev.aboutme.domain.common.JavaDateToSqlTimeStamp;
import ru.saidgadjiev.ormnext.core.field.*;
import ru.saidgajiev.ormnext.cache.Cacheable;

import java.util.Date;

@Cacheable
public class Comment {

    @DatabaseColumn(id = true, generated = true, persisterClass = SerialTypeDataPersister.class)
    private Integer id;

    @DatabaseColumn(notNull = true, persisterClass = TextTypeDataPersister.class)
    private String content;

    @DatabaseColumn(notNull = true)
    @ForeignColumn(fetchType = FetchType.LAZY, onDelete = ReferenceAction.CASCADE, onUpdate = ReferenceAction.CASCADE)
    private Post post;

    @Converter(value = JavaDateToSqlTimeStamp.class)
    @DatabaseColumn(notNull = true, dataType = DataType.TIMESTAMP)
    private Date createdDate = new Date();

    @DatabaseColumn(notNull = true)
    @ForeignColumn(foreignFieldName = "userName")
    private UserProfile2 user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserProfile2 getUser() {
        return user;
    }

    public void setUser(UserProfile2 user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", user=" + (user == null ? null : user.getUserName()) +
                '}';
    }
}
