package soat.project.fastfoodsoat.infrastructure.client.presenter;

import soat.project.fastfoodsoat.application.output.client.auth.AuthClientOutput;
import soat.project.fastfoodsoat.infrastructure.client.model.response.ClientAuthResponse;

public interface AuthClientPresenter {
    static ClientAuthResponse present(final AuthClientOutput output) {
        return new ClientAuthResponse(
                output.publicId(),
                output.name(),
                output.email(),
                output.cpf()
        );
    }
}
