table! {
    products (id) {
        id -> Text,
        name -> Text,
        barcode -> Text,
        price -> Integer,
    }
}

table! {
    suppliers (id) {
        id -> Text,
        name -> Text,
        telephone -> Text,
        website -> Text,
        email -> Text,
    }
}

table! {
    users (id) {
        id -> Text,
        name -> Text,
        telephone -> Text,
        email -> Text,
        password_hash -> Text,
        code -> Text,
    }
}

table! {
    versions (release_sequence_num) {
        release_sequence_num -> Integer,
        major -> Integer,
        minor -> Integer,
        installed -> Bool,
        release_date -> Timestamp,
    }
}

allow_tables_to_appear_in_same_query!(
    products,
    suppliers,
    users,
    versions,
);
