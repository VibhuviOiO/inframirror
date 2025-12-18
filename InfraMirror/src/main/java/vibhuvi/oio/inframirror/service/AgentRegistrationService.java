package vibhuvi.oio.inframirror.service;

import vibhuvi.oio.inframirror.service.dto.AgentRegistrationRequestDTO;
import vibhuvi.oio.inframirror.service.dto.AgentRegistrationResponseDTO;

/**
 * Service Interface for agent self-registration.
 */
public interface AgentRegistrationService {
    /**
     * Register a new agent and auto-create Region/Datacenter if needed.
     *
     * @param request the agent registration request
     * @return the registration response with agentId and API key
     */
    AgentRegistrationResponseDTO registerAgent(AgentRegistrationRequestDTO request);
}
