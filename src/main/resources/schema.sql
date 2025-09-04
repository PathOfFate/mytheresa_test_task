create table products (
    sku         varchar        not null primary key,
    price       decimal(10, 2) not null,
    description varchar        not null,
    category    varchar        not null
);
