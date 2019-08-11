use serde::Serialize;

#[derive(Serialize)]
pub struct Language {
    pub name: &'static str,
    pub code: &'static str,
}

impl Language {
    pub fn new(name: &'static str, code: &'static str) -> Language {
        Language {
            name: name,
            code: code,
        }
    }
}
