package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.StreamChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamChannelRepository extends JpaRepository<StreamChannel, Long> {
}
