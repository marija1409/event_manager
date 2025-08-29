package org.example.eventsbooker;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.example.eventsbooker.filters.CorsFilter;
import org.example.eventsbooker.repositories.category.CategoryRepository;
import org.example.eventsbooker.repositories.category.MySqlCategoryRepository;
import org.example.eventsbooker.repositories.comment.CommentRepository;
import org.example.eventsbooker.repositories.comment.MySqlCommentRepository;
import org.example.eventsbooker.repositories.engagement.EngagementRepository;
import org.example.eventsbooker.repositories.engagement.MySqlEngagementRepository;
import org.example.eventsbooker.repositories.event.EventRepository;
import org.example.eventsbooker.repositories.event.MySqlEventRepository;
import org.example.eventsbooker.repositories.user.MySqlUserRepository;
import org.example.eventsbooker.repositories.user.UserRepository;
import org.example.eventsbooker.services.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/api")
public class App extends ResourceConfig {
    public App() {
        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(MySqlUserRepository.class).to(UserRepository.class).in(Singleton.class);
                this.bindAsContract(UserService.class);
                this.bind(MySqlCategoryRepository.class).to(CategoryRepository.class).in(Singleton.class);
                this.bindAsContract(CategoryService.class);
                this.bind(MySqlEventRepository.class).to(EventRepository.class).in(Singleton.class);
                this.bindAsContract(EventService.class);
                this.bind(MySqlCommentRepository.class).to(CommentRepository.class).in(Singleton.class);
                this.bindAsContract(CommentService.class);
                this.bind(MySqlEngagementRepository.class).to(EngagementRepository.class).in(Singleton.class);
                this.bindAsContract(EngagementService.class);
            }
        };
        register(binder);
        packages("org.example.eventsbooker");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        register(CorsFilter.class);

    }
}
