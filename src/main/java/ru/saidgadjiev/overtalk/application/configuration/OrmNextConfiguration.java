package ru.saidgadjiev.overtalk.application.configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.saidgadjiev.orm.next.core.cache.LRUObjectCache;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.SessionManager;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.logger.LoggerFactory;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;
import ru.saidgadjiev.orm.next.core.utils.TableUtils;
import ru.saidgadjiev.overtalk.application.dao.CommentDao;
import ru.saidgadjiev.overtalk.application.dao.PostDao;
import ru.saidgadjiev.overtalk.application.domain.Comment;
import ru.saidgadjiev.overtalk.application.domain.Post;
import ru.saidgadjiev.overtalk.application.service.BlogService;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */
@Configuration
public class OrmNextConfiguration {

    @Bean
    @Scope(scopeName = "singleton")
    public SessionManager sessionManager() throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setUser("root");
        dataSource.setPassword("said1995");
        dataSource.setURL("jdbc:mysql://localhost:3306/overtalk");
        SessionManager sessionManager = new BaseSessionManagerImpl(new PolledConnectionSource(dataSource, new H2DatabaseType()));

        sessionManager.setObjectCache(new LRUObjectCache(16), Post.class, Comment.class);
        TableUtils.createTable(sessionManager.getDataSource(), Post.class, true);
        TableUtils.createTable(sessionManager.getDataSource(), Comment.class, true);

        return sessionManager;
    }

    @Bean
    public BlogService blogService() throws SQLException {
        Session<Post, Integer> postSession = sessionManager().forClass(Post.class);
        PostDao postDao = new PostDao(postSession);
        Session<Comment, Integer> commentSession = sessionManager().forClass(Comment.class);
        CommentDao commentDao = new CommentDao(commentSession);

        return new BlogService(postDao, commentDao);
    }

}