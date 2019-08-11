use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct LoginAttempt {
    pub code: String
}
