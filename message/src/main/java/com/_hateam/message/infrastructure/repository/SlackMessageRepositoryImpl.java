package com._hateam.message.infrastructure.repository;

import com._hateam.message.domain.model.SlackMessage;
import com._hateam.message.domain.repository.SlackMessageRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class SlackMessageRepositoryImpl implements SlackMessageRepository {

    private final EntityManager entityManager;
    private final SlackMessageRepositoryCustomImpl customRepository;
    private final SimpleJpaRepository<SlackMessage, UUID> jpaRepository;

    public SlackMessageRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaRepository = new SimpleJpaRepository<>(SlackMessage.class, entityManager);
        this.customRepository = new SlackMessageRepositoryCustomImpl(new com.querydsl.jpa.impl.JPAQueryFactory(entityManager));
    }

    @Override
    public Optional<SlackMessage> findByIdNotDeleted(UUID messageId) {
        return jpaRepository.findById(messageId)
                .filter(message -> message.getDeletedAt() == null);
    }

    @Override
    public Page<SlackMessage> findAllNotDeleted(Pageable pageable) {
        return searchMessages(null, null, null, null, pageable);
    }

    @Override
    public Page<SlackMessage> searchMessages(String receiverId, LocalDateTime startDate,
                                             LocalDateTime endDate, String keyword, Pageable pageable) {
        return customRepository.searchMessages(receiverId, startDate, endDate, keyword, pageable);
    }

    @Override
    public <S extends SlackMessage> S save(S entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public <S extends SlackMessage> List<S> saveAll(Iterable<S> entities) {
        return jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<SlackMessage> findById(UUID uuid) {
        return jpaRepository.findById(uuid);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return jpaRepository.existsById(uuid);
    }

    @Override
    public List<SlackMessage> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<SlackMessage> findAllById(Iterable<UUID> uuids) {
        return jpaRepository.findAllById(uuids);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(UUID uuid) {
        jpaRepository.deleteById(uuid);
    }

    @Override
    public void delete(SlackMessage entity) {
        jpaRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        jpaRepository.deleteAllById(uuids);
    }

    @Override
    public void deleteAll(Iterable<? extends SlackMessage> entities) {
        jpaRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public Page<SlackMessage> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public <S extends SlackMessage> S saveAndFlush(S entity) {
        return jpaRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends SlackMessage> List<S> saveAllAndFlush(Iterable<S> entities) {
        return jpaRepository.saveAllAndFlush(entities);
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }

    @Override
    public void deleteAllInBatch(Iterable<SlackMessage> entities) {
        jpaRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        jpaRepository.deleteAllByIdInBatch(uuids);
    }

    @Override
    public void deleteAllInBatch() {
        jpaRepository.deleteAllInBatch();
    }

    @Override
    public SlackMessage getOne(UUID uuid) {
        return jpaRepository.getReferenceById(uuid);
    }

    @Override
    public SlackMessage getById(UUID uuid) {
        return jpaRepository.getReferenceById(uuid);
    }

    @Override
    public SlackMessage getReferenceById(UUID uuid) {
        return jpaRepository.getReferenceById(uuid);
    }

    @Override
    public <S extends SlackMessage> Optional<S> findOne(Example<S> example) {
        return jpaRepository.findOne(example);
    }

    @Override
    public <S extends SlackMessage> List<S> findAll(Example<S> example) {
        return jpaRepository.findAll(example);
    }

    @Override
    public <S extends SlackMessage> List<S> findAll(Example<S> example, Sort sort) {
        return jpaRepository.findAll(example, sort);
    }

    @Override
    public <S extends SlackMessage> Page<S> findAll(Example<S> example, Pageable pageable) {
        return jpaRepository.findAll(example, pageable);
    }

    @Override
    public <S extends SlackMessage> long count(Example<S> example) {
        return jpaRepository.count(example);
    }

    @Override
    public <S extends SlackMessage> boolean exists(Example<S> example) {
        return jpaRepository.exists(example);
    }

    @Override
    public List<SlackMessage> findAll(Sort sort) {
        return jpaRepository.findAll(sort);
    }

    @Override
    public <S extends SlackMessage, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return jpaRepository.findBy(example, queryFunction);
    }
}