create table products (
    sku         varchar        primary key,
    price       decimal(10, 2) not null,
    description varchar        not null,
    category    varchar        not null
);
