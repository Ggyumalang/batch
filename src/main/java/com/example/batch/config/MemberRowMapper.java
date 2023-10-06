package com.example.batch.config;

import com.example.batch.domain.Member;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MemberRowMapper implements RowMapper<Member> {
    @Override
    public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Member.builder()
                .id(rs.getLong("id"))
                .memberId(rs.getString("member_id"))
                .name(rs.getString("name"))
                .password(rs.getString("password"))
                .build();
    }
}
