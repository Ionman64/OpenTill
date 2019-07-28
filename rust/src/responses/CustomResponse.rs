use serde::Serialize;

#[derive(Serialize)]
pub struct CustomResponse {
    pub success: bool,
    pub message: Option<String>,
}

impl CustomResponse {
    pub fn success() -> CustomResponse {
        CustomResponse {
            success: true,
            message: None,
        }
    }
    pub fn fail() -> CustomResponse {
        CustomResponse {
            success: false,
            message: None,
        }
    }
    pub fn fail_with_message(m: String) -> CustomResponse {
        CustomResponse {
            success: false,
            message: Some(m),
        }
    }
}
