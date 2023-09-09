package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.github.javafaker.Faker;

import java.time.Year;
import java.util.Set;

import static io.vavr.API.*;

public final class Fixture {

    private Fixture() {
    }

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static Double duration() {
        return FAKER.options().option(120.0, 15.5, 35.5, 10.0, 2.0);
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String title() {
        return FAKER.options().option(
                "System Design no Mercado Livre na prática",
                "Não cometa esses erros ao trabalhar com Microsserviços",
                "Testes de Mutação. Você não testa seu software corretamente"
        );
    }

    public static String checksum() {
        return "03fe62de";
    }

    public static Video video() {
        final var aBuilder = new Video.Builder(
                Fixture.title(),
                Videos.description(),
                Year.of(Fixture.year()),
                Videos.rating())
                .releaseStatus(Videos.releaseStatus())
                .publishingStatus(Videos.publishingStatus())
                .categories(Set.of(Categories.aulas().getId()))
                .genres(Set.of(Genres.tech().getId()))
                .castMembers(Set.of(CastMembers.wesley().getId()));
        return Video.newVideo(aBuilder);
    }

    public static final class Categories {
        private static final Category AULAS =
                Category.newCategory("Aulas", "Some description");
        private static final Category LIVES =
                Category.newCategory("Lives", "Some description");

        public static Category aulas() {
            return Category.with(AULAS);
        }

        public static Category lives() {
            return Category.with(LIVES);
        }
    }

    public static final class CastMembers {

        private static final CastMember WESLEY = CastMember
                .newCastMember("Wesley FullCycle", CastMemberType.ACTOR);
        private static final CastMember GABRIEL = CastMember
                .newCastMember("Gabriel FullCycle", CastMemberType.ACTOR);

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember wesley() {
            return CastMember.with(WESLEY);
        }

        public static CastMember gabriel() {
            return CastMember.with(GABRIEL);
        }
    }

    public static final class Genres {
        private static final Genre TECH = Genre.newGenre("Technology");
        private static final Genre BUSINESS = Genre.newGenre("Business");

        public static Genre tech() {
            return Genre.with(TECH);
        }

        public static Genre business() {
            return Genre.with(BUSINESS);
        }
    }

    public static final class Videos {

        private static final Video.Builder SYSTEM_DESIGN = new Video.Builder(
                Fixture.title(),
                Videos.description(),
                Year.of(Fixture.year()),
                Videos.rating())
                .releaseStatus(Videos.releaseStatus())
                .publishingStatus(Videos.publishingStatus())
                .categories(Set.of(Categories.aulas().getId()))
                .genres(Set.of(Genres.tech().getId()))
                .castMembers(Set.of(CastMembers.wesley().getId()));

        public static Video systemDesign() {
            return Video.newVideo(SYSTEM_DESIGN);
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static VideoMediaType mediaType() {
            return FAKER.options().option(VideoMediaType.values());
        }

        public static String description() {
            return FAKER.options().option(
                    """
                            Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                            Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                            Para acessar todas as aulas, lives e desafios, acesse:
                            https://imersao.fullcycle.com.br/
                            """,
                    """
                            Nesse vídeo você entenderá o que é DTO (Data Transfer Object), quando e como utilizar no dia a dia,
                            bem como sua importância para criar aplicações com alta qualidade.
                            """
            );
        }

        public static AudioVideoMedia audioVideo(final VideoMediaType type) {
            final var checksum = Fixture.checksum();
            return AudioVideoMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/videos/" + checksum
            );
        }

        public static ImageMedia image(final VideoMediaType type) {
            final var checksum = Fixture.checksum();
            return ImageMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/images/" + checksum
            );
        }

        public static ReleaseStatus releaseStatus() {
            return FAKER.options().option(ReleaseStatus.values());
        }

        public static PublishingStatus publishingStatus() {
            return FAKER.options().option(PublishingStatus.values());
        }

        public static Resource resource(final VideoMediaType aType) {
            final String contentType = Match(aType).of(
                    Case($(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                    Case($(), "image/jpg")
            );
            final var checksum = Fixture.checksum();
            final byte[] content = "Conteudo".getBytes();
            return Resource.with(checksum, content, contentType,
                    aType.name().toLowerCase());
        }
    }
}
