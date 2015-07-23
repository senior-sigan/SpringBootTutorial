package it.sevenbits.springboottutorial.core.mappers;

import it.sevenbits.springboottutorial.core.domain.Subscription;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SubscriptionMapper {

    @Select("SELECT id, subscriber_name, email FROM subscriptions")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "email", property = "email"),
        @Result(column = "subscriber_name", property = "name")
    })
    List<Subscription> findAll();

    @Select("SELECT id, subscriber_name, email FROM subscriptions WHERE id = #{id}")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "email", property = "email"),
        @Result(column = "subscriber_name", property = "name")
    })
    Subscription find(@Param("id") final Long id);

    @Insert("INSERT INTO subscriptions (email, subscriber_name) VALUES (#{email}, #{name})")
    void save(final Subscription subscription);
    
    @Delete("DELETE FROM subscriptions WHERE id = #{id}")
    void delete(@Param("id") final Long id);
}
