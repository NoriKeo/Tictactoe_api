create table if not exists public.accounts
(
    player_id         serial
        primary key,
    player_name       varchar(255),
    passwort          varchar(255),
    security_question varchar(255)
);

alter table public.accounts
    owner to postgres;
create table if not exists public.verdict
(
    id     SERIAL primary key NOT NULL ,
    reason varchar(255)
);

alter table public.verdict
    owner to postgres;

create table if not exists public.score
(
    score_id       serial
        primary key,
    player_id      integer not null
        references public.accounts,
    computer_score integer,
    player_score   integer,
    draw_score     integer
);

alter table public.score
    owner to postgres;

create table if not exists public.match
(
    id         SERIAL primary key,
    player_id  integer not null
        references public.accounts,
    started_at timestamp,
    ended_at   timestamp,
    verdict_id integer not null references public.verdict


);

alter table public.match
    owner to postgres;

create table if not exists public.move
(
    id         SERIAL primary key,
    match_id   integer not null
        references public.match,
    is_player  boolean,
    position   integer not null,
    created_at timestamp,
    move_nr    SERIAL  NOT NULL
);

alter table public.move
    owner to postgres;






