package soat.project.fastfoodsoat.infrastructure.client.controller;

import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.application.command.client.auth.AuthClientCommand;
import soat.project.fastfoodsoat.application.command.client.create.CreateClientCommand;
import soat.project.fastfoodsoat.application.usecase.client.auth.AuthClientUseCase;
import soat.project.fastfoodsoat.application.usecase.client.create.CreateClientUseCase;
import soat.project.fastfoodsoat.infrastructure.client.model.request.ClientAuthRequest;
import soat.project.fastfoodsoat.infrastructure.client.model.request.CreateClientRequest;
import soat.project.fastfoodsoat.infrastructure.client.model.response.ClientAuthResponse;
import soat.project.fastfoodsoat.infrastructure.client.model.response.CreateClientResponse;
import soat.project.fastfoodsoat.infrastructure.client.presenter.AuthClientPresenter;
import soat.project.fastfoodsoat.infrastructure.client.presenter.ClientPresenter;

@Component
public class ClientControllerImpl implements ClientController {

    private final AuthClientUseCase authClientUseCase;
    private final CreateClientUseCase createClientUseCase;

    public ClientControllerImpl(
            final AuthClientUseCase authClientUseCase,
            final CreateClientUseCase createClientUseCase
    ) {
        this.authClientUseCase = authClientUseCase;
        this.createClientUseCase = createClientUseCase;
    }

    @Override
    public ClientAuthResponse authClient(final ClientAuthRequest request) {
        final var command = new AuthClientCommand(request.identification());

        final var output = this.authClientUseCase.execute(command);

        return AuthClientPresenter.present(output);
    }

    @Override
    public CreateClientResponse createClient(final CreateClientRequest request) {
        var command = new CreateClientCommand(request.name(), request.email(), request.cpf());

        var output = this.createClientUseCase.execute(command);

        return ClientPresenter.present(output);
    }

}
