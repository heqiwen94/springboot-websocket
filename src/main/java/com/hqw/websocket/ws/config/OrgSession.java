package com.hqw.websocket.ws.config;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Objects;

@Data
public class OrgSession implements Principal {
    private String orgId;
    private String orgName;
    private WebSocketSession session;

    @Override
    public String getName() {
        return orgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgSession that = (OrgSession) o;
        return Objects.equals(orgId, that.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId);
    }
}
