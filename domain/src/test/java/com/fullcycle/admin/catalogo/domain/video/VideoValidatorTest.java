package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VideoValidatorTest extends UnitTest {

    @DisplayName("Validate a video with invalid params")
    @Nested
    class ValidateWithInvalidParams {

        @Test
        void Given_null_title_When_calls_validate_Then_should_receive_an_error() {
            // given
            final String aNullTitle = null;
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'title' should not be null";

            final var aBuilder = new Video.Builder(aNullTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_empty_title_When_calls_validate_Then_should_receive_an_error() {
            // Given
            final String anEmptyTitle = " ";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'title' should not be empty";

            final var aBuilder = new Video.Builder(anEmptyTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_title_length_greater_than_255_When_calls_validate_Then_should_receive_an_error() {
            // Given
            final String expectedTitle = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais. Esse vídeo faz parte da Imersão Full Stack && Full Cycle. Para acessar todas as aulas, lives e desafios, acesse: https://imersao.fullcycle.com.br/. A certificação de metodologias que nos auxiliam a lidar com o surgimento do comércio virtual agrega valor ao estabelecimento das direções preferenciais no sentido do progresso.
                    """;
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'title' must be between 1 and 255 characters";

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_null_description_When_calls_validate_Then_should_receive_an_error() {
            // Given
            final String expectedTitle = "System Design Interviews";
            final String aNullDescription = null;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'description' should not be null";

            final var aBuilder = new Video.Builder(expectedTitle,
                    aNullDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_empty_description_When_calls_validate_Then_should_receive_an_error() {
            // Given
            final String expectedTitle = "System Design Interviews";
            final var anEmptyDescription = " ";
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'description' should not be empty";

            final var aBuilder = new Video.Builder(expectedTitle,
                    anEmptyDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_description_length_greater_than_4000_When_calls_validate_Then_should_receive_an_error() {
            // Given
            final String expectedTitle = "System Design Interviews";
            final var aGreaterThan4000Description = """
                    O empenho em analisar a execução dos pontos do programa ainda não demonstrou convincentemente que vai participar na mudança do investimento em reciclagem técnica. Podemos já vislumbrar o modo pelo qual o acompanhamento das preferências de consumo representa uma abertura para a melhoria das direções preferenciais no sentido do progresso. A prática cotidiana prova que a contínua expansão de nossa atividade faz parte de um processo de gerenciamento dos modos de operação convencionais. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que o julgamento imparcial das eventualidades deve passar por modificações independentemente dos modos de operação convencionais. Ainda assim, existem dúvidas a respeito de como a revolução dos costumes garante a contribuição de um grupo importante na determinação dos níveis de motivação departamental. Percebemos, cada vez mais, que a valorização de fatores subjetivos obstaculiza a apreciação da importância das condições financeiras e administrativas exigidas. Ainda assim, existem dúvidas a respeito de como a execução dos pontos do programa maximiza as possibilidades por conta dos modos de operação convencionais. Desta maneira, o fenômeno da Internet facilita a criação das posturas dos órgãos dirigentes com relação às suas atribuições. O empenho em analisar a execução dos pontos do programa ainda não demonstrou convincentemente que vai participar na mudança do investimento em reciclagem técnica. Podemos já vislumbrar o modo pelo qual o acompanhamento das preferências de consumo representa uma abertura para a melhoria das direções preferenciais no sentido do progresso. A prática cotidiana prova que a contínua expansão de nossa atividade faz parte de um processo de gerenciamento dos modos de operação convencionais. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que o julgamento imparcial das eventualidades deve passar por modificações independentemente dos modos de operação convencionais. Ainda assim, existem dúvidas a respeito de como a revolução dos costumes garante a contribuição de um grupo importante na determinação dos níveis de motivação departamental. Percebemos, cada vez mais, que a valorização de fatores subjetivos obstaculiza a apreciação da importância das condições financeiras e administrativas exigidas. Ainda assim, existem dúvidas a respeito de como a execução dos pontos do programa maximiza as possibilidades por conta dos modos de operação convencionais. Desta maneira, o fenômeno da Internet facilita a criação das posturas dos órgãos dirigentes com relação às suas atribuições. O empenho em analisar a execução dos pontos do programa ainda não demonstrou convincentemente que vai participar na mudança do investimento em reciclagem técnica. Podemos já vislumbrar o modo pelo qual o acompanhamento das preferências de consumo representa uma abertura para a melhoria das direções preferenciais no sentido do progresso. A prática cotidiana prova que a contínua expansão de nossa atividade faz parte de um processo de gerenciamento dos modos de operação convencionais. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que o julgamento imparcial das eventualidades deve passar por modificações independentemente dos modos de operação convencionais. Ainda assim, existem dúvidas a respeito de como a revolução dos costumes garante a contribuição de um grupo importante na determinação dos níveis de motivação departamental. Percebemos, cada vez mais, que a valorização de fatores subjetivos obstaculiza a apreciação da importância das condições financeiras e administrativas exigidas. Ainda assim, existem dúvidas a respeito de como a execução dos pontos do programa maximiza as possibilidades por conta dos modos de operação convencionais. Desta maneira, o fenômeno da Internet facilita a criação das posturas dos órgãos dirigentes com relação às suas atribuições. O empenho em analisar a execução dos pontos do programa ainda não demonstrou convincentemente que vai participar na mudança do investimento em reciclagem técnica. Podemos já vislumbrar o modo pelo qual o acompanhamento das preferências de consumo representa uma abertura para a melhoria das direções preferenciais no sentido do progresso. A prática cotidiana prova que a contínua expansão de nossa atividade faz parte de um processo de gerenciamento dos modos de operação convencionais. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que o julgamento imparcial das eventualidades deve passar por modificações independentemente dos modos de operação convencionais. Ainda assim, existem dúvidas a respeito de como a revolução dos costumes garante a contribuição de um grupo importante na determinação dos níveis de motivação departamental. Percebemos, cada vez mais, que a valorização de fatores subjetivos obstaculiza a apreciação da importância das condições financeiras e administrativas exigidas. Ainda assim, existem dúvidas a respeito de como a execução dos pontos do programa maximiza as possibilidades por conta dos modos de operação convencionais. Desta maneira, o fenômeno da Internet facilita a criação das posturas dos órgãos dirigentes com relação às suas atribuições.
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'description' must be between 1 and 4000 characters";

            final var aBuilder = new Video.Builder(expectedTitle,
                    aGreaterThan4000Description,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_null_launchedAt_When_calls_validate_Then_should_receive_an_error() {
            // given
            final String expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final Year aNullLaunchedAt = null;
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'launchedAt' should not be null";

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    aNullLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_null_rating_When_calls_validate_Then_should_receive_an_error() {
            // given
            final String expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final Rating aNullRating = null;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var expectedErrorCount = 1;
            final var expectedErrorMessage = "'rating' should not be null";

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    aNullRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // When
            Executable invalidMethodCall = () -> Video.newVideo(aBuilder);

            // Then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }
    }
}
