package soat.project.fastfoodsoat.application.usecase.order.retrieve.list.forstaff;

import soat.project.fastfoodsoat.application.command.order.retrieve.list.ListOrderForStaffCommand;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.application.output.order.retrieve.list.ListOrderOutput;
import soat.project.fastfoodsoat.domain.pagination.Pagination;

public class ListOrderForStaffUseCaseImpl extends ListOrderForStaffUseCase {

    private final OrderRepositoryGateway orderRepositoryGateway;

    public ListOrderForStaffUseCaseImpl(final OrderRepositoryGateway orderRepositoryGateway) {
        this.orderRepositoryGateway = orderRepositoryGateway;
    }

    @Override
    public Pagination<ListOrderOutput> execute(ListOrderForStaffCommand command) {
        final var onlyPaid = command.onlyPaid();
        final var query = command.searchQuery();

        return orderRepositoryGateway.findAllForStaff(query)
                .map(ListOrderOutput::from);
    }
}
