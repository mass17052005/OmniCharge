package com.omnicharge.operatorservice.service;

import com.omnicharge.operatorservice.dto.*;
import com.omnicharge.operatorservice.entity.*;
import com.omnicharge.operatorservice.enums.*;
import com.omnicharge.operatorservice.exception.*;
import com.omnicharge.operatorservice.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorServiceTest {

    // ── Mocks ────────────────────────────────────────────────────────
    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private RechargePlanRepository rechargePlanRepository;

    // ── Real service with mocks injected ────────────────────────────
    @InjectMocks
    private OperatorService operatorService;

    // ── Test Data ────────────────────────────────────────────────────
    private Operator sampleOperator;
    private RechargePlan samplePlan;
    private OperatorRequest sampleOperatorRequest;
    private PlanRequest samplePlanRequest;

    @BeforeEach
    void setUp() {
        sampleOperator = Operator.builder()
                .id(1L)
                .name("Jio")
                .code("JIO")
                .status(OperatorStatus.ACTIVE)
                .build();

        samplePlan = RechargePlan.builder()
                .id(1L)
                .operator(sampleOperator)
                .planName("Jio 239")
                .price(new BigDecimal("239"))
                .validityDays(28)
                .dataPerDay("1.5GB")
                .description("1.5GB/day for 28 days")
                .status(PlanStatus.ACTIVE)
                .build();

        sampleOperatorRequest = new OperatorRequest("Jio", "JIO");

        samplePlanRequest = new PlanRequest(
                "Jio 239",
                new BigDecimal("239"),
                28,
                "1.5GB",
                "1.5GB/day for 28 days"
        );
    }

    // ════════════════════════════════════════════════════════════════
    // ADD OPERATOR TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Add operator - success")
    void addOperator_ShouldReturnOperatorResponse_WhenValidRequest() {
        // Arrange
        when(operatorRepository.existsByCode("JIO"))
                .thenReturn(false);
        when(operatorRepository.save(any(Operator.class)))
                .thenReturn(sampleOperator);

        // Act
        OperatorResponse operatorResponse =
                operatorService.addOperator(sampleOperatorRequest);

        // Assert
        assertThat(operatorResponse).isNotNull();
        assertThat(operatorResponse.getName()).isEqualTo("Jio");
        assertThat(operatorResponse.getCode()).isEqualTo("JIO");
        assertThat(operatorResponse.getStatus())
                .isEqualTo(OperatorStatus.ACTIVE);
        verify(operatorRepository, times(1))
                .save(any(Operator.class));
    }

    @Test
    @DisplayName("Add operator - fails when duplicate code exists")
    void addOperator_ShouldThrowException_WhenDuplicateCode() {
        // Arrange
        when(operatorRepository.existsByCode("JIO"))
                .thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.addOperator(sampleOperatorRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("JIO");

        verify(operatorRepository, never())
                .save(any(Operator.class));
    }

    // ════════════════════════════════════════════════════════════════
    // GET ALL OPERATORS TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Get all operators - returns list of active operators")
    void getAllActiveOperators_ShouldReturnList_WhenOperatorsExist() {
        // Arrange
        when(operatorRepository.findByStatus(OperatorStatus.ACTIVE))
                .thenReturn(List.of(sampleOperator));

        // Act
        List<OperatorResponse> operatorResponseList =
                operatorService.getAllActiveOperators();

        // Assert
        assertThat(operatorResponseList).isNotEmpty();
        assertThat(operatorResponseList).hasSize(1);
        assertThat(operatorResponseList.get(0).getName())
                .isEqualTo("Jio");
    }

    @Test
    @DisplayName("Get all operators - returns empty list when none exist")
    void getAllActiveOperators_ShouldReturnEmptyList_WhenNoOperators() {
        // Arrange
        when(operatorRepository.findByStatus(OperatorStatus.ACTIVE))
                .thenReturn(List.of());

        // Act
        List<OperatorResponse> operatorResponseList =
                operatorService.getAllActiveOperators();

        // Assert
        assertThat(operatorResponseList).isEmpty();
    }

    // ════════════════════════════════════════════════════════════════
    // GET OPERATOR BY ID TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Get operator by ID - success")
    void getOperatorById_ShouldReturnOperator_WhenExists() {
        // Arrange
        when(operatorRepository.findById(1L))
                .thenReturn(Optional.of(sampleOperator));

        // Act
        OperatorResponse operatorResponse =
                operatorService.getOperatorById(1L);

        // Assert
        assertThat(operatorResponse).isNotNull();
        assertThat(operatorResponse.getOperatorId()).isEqualTo(1L);
        assertThat(operatorResponse.getName()).isEqualTo("Jio");
    }

    @Test
    @DisplayName("Get operator by ID - throws 404 when not found")
    void getOperatorById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(operatorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.getOperatorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ════════════════════════════════════════════════════════════════
    // UPDATE OPERATOR TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Update operator - success")
    void updateOperator_ShouldReturnUpdatedOperator_WhenExists() {
        // Arrange
        OperatorRequest updateRequest =
                new OperatorRequest("Jio Updated", "JIO");
        when(operatorRepository.findById(1L))
                .thenReturn(Optional.of(sampleOperator));
        when(operatorRepository.save(any(Operator.class)))
                .thenReturn(sampleOperator);

        // Act
        OperatorResponse operatorResponse =
                operatorService.updateOperator(1L, updateRequest);

        // Assert
        assertThat(operatorResponse).isNotNull();
        verify(operatorRepository, times(1))
                .save(any(Operator.class));
    }

    @Test
    @DisplayName("Update operator - throws 404 when not found")
    void updateOperator_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(operatorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.updateOperator(99L, sampleOperatorRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ════════════════════════════════════════════════════════════════
    // ADD PLAN TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Add plan - success")
    void addPlanToOperator_ShouldReturnPlanResponse_WhenValidRequest() {
        // Arrange
        when(operatorRepository.findById(1L))
                .thenReturn(Optional.of(sampleOperator));
        when(rechargePlanRepository.save(any(RechargePlan.class)))
                .thenReturn(samplePlan);

        // Act
        PlanResponse planResponse =
                operatorService.addPlanToOperator(1L, samplePlanRequest);

        // Assert
        assertThat(planResponse).isNotNull();
        assertThat(planResponse.getPlanName()).isEqualTo("Jio 239");
        assertThat(planResponse.getPrice())
                .isEqualByComparingTo(new BigDecimal("239"));
        verify(rechargePlanRepository, times(1))
                .save(any(RechargePlan.class));
    }

    @Test
    @DisplayName("Add plan - throws 404 when operator not found")
    void addPlanToOperator_ShouldThrowException_WhenOperatorNotFound() {
        // Arrange
        when(operatorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.addPlanToOperator(99L, samplePlanRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(rechargePlanRepository, never())
                .save(any(RechargePlan.class));
    }

    // ════════════════════════════════════════════════════════════════
    // GET PLANS BY OPERATOR TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Get all plans - returns plans for operator")
    void getAllPlansByOperator_ShouldReturnList_WhenPlansExist() {
        // Arrange
        when(operatorRepository.findById(1L))
                .thenReturn(Optional.of(sampleOperator));
        when(rechargePlanRepository
                .findByOperatorIdAndStatus(1L, PlanStatus.ACTIVE))
                .thenReturn(List.of(samplePlan));

        // Act
        List<PlanResponse> planResponseList =
                operatorService.getAllPlansByOperator(1L);

        // Assert
        assertThat(planResponseList).isNotEmpty();
        assertThat(planResponseList).hasSize(1);
        assertThat(planResponseList.get(0).getPlanName())
                .isEqualTo("Jio 239");
    }

    @Test
    @DisplayName("Get all plans - throws 404 when operator not found")
    void getAllPlansByOperator_ShouldThrowException_WhenOperatorNotFound() {
        // Arrange
        when(operatorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.getAllPlansByOperator(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ════════════════════════════════════════════════════════════════
    // GET PLAN BY ID TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Get plan by ID - success")
    void getPlanByIdAndOperator_ShouldReturnPlan_WhenExists() {
        // Arrange
        when(rechargePlanRepository.findByIdAndOperatorId(1L, 1L))
                .thenReturn(Optional.of(samplePlan));

        // Act
        PlanResponse planResponse =
                operatorService.getPlanByIdAndOperator(1L, 1L);

        // Assert
        assertThat(planResponse).isNotNull();
        assertThat(planResponse.getPlanId()).isEqualTo(1L);
        assertThat(planResponse.getPlanName()).isEqualTo("Jio 239");
    }

    @Test
    @DisplayName("Get plan by ID - throws 404 when not found")
    void getPlanByIdAndOperator_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(rechargePlanRepository.findByIdAndOperatorId(99L, 1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.getPlanByIdAndOperator(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ════════════════════════════════════════════════════════════════
    // DELETE OPERATOR TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Delete operator - success")
    void deleteOperator_ShouldDeleteOperator_WhenExists() {
        // Arrange
        when(operatorRepository.findById(1L))
                .thenReturn(Optional.of(sampleOperator));

        // Act
        operatorService.deleteOperator(1L);

        // Assert
        verify(operatorRepository, times(1)).delete(sampleOperator);
    }

    @Test
    @DisplayName("Delete operator - throws 404 when not found")
    void deleteOperator_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(operatorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                operatorService.deleteOperator(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(operatorRepository, never()).delete(any());
    }
}
