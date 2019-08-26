use utils as app;
use std::fs;
use std::path::{Path, PathBuf};
use std::fs::File;
use regex::Regex;
use std::io::Write;
use std::ops::Index;
use std::io::BufReader;
use std::io::BufRead;

pub fn process_templates_into_folder() {
    let input_directory = app::get_home_dir().join("input_templates");
    let directory = match fs::read_dir(input_directory) {
        Ok(x) => x,
        Err(x) => {
            panic!("Could not read input templates directory: {}", x);
        }
    };
    let regex_pattern = r#"<modal data-page="(.*)">"#;
    let re = match Regex::new(regex_pattern) {
        Ok(x) => x,
        Err(x) => {
            panic!("Could not compile regex, {}", x);
        }
    };
    for dir_entry_result in directory {
        let dir_entry = match dir_entry_result {
            Ok(x) => x,
            Err(x) => {
                panic!("{}", x);
            }
        };
        let path = dir_entry.path();
        let extension = match Path::new(&path).extension() {
            Some(x) => x,
            None => {
                continue;
            }
        };
        let filename = match Path::new(&path).file_name() {
            Some(x) => x,
            None => {
                continue;
            }
        };
        match extension.to_os_string().to_str() {
            Some(x) => {
                if x == "hbs" {
                    let input_file = match File::open(&path) {
                        Ok(x) => x,
                        Err(x) => {
                            panic!("Could not read file {}", x);
                        }
                    };
                    let mut output_file = match File::create(app::get_app_dir().join("templates").join(filename)) {
                        Ok(x) => x,
                        Err(x) => {
                            panic!("Could not create file {}", x);
                        }
                    };
                    for unwrapped_line in BufReader::new(input_file).lines() {
                        let line = match unwrapped_line {
                            Ok(x) => x,
                            Err(x) => {
                                continue;
                            }
                        };
                        let cap = re.captures(&line);
                        let found_path = match cap {
                            Some(x) => x,
                            None => {
                                writeln!(output_file, "{}", &line);
                                continue;
                            }
                        };
                        let path = PathBuf::from(app::get_app_dir().join("input_templates").join(found_path.index(1).replace("/", &std::path::MAIN_SEPARATOR.to_string()).replace("\\", &std::path::MAIN_SEPARATOR.to_string())));
                        if path.exists() {
                            output_file.write(&app::read_all_lines(path).into_bytes());
                        }
                        else {
                            warn!("{} does not exist for inclusion into the template file", path.display());
                        }
                        
                    }
                    match output_file.sync_all() {
                        Ok(x) => {},
                        Err(x) => {
                            panic!("{}", x);
                        }
                    }
                }
                else {
                    println!("not hbs {}", x);
                }
            }
            None => {
                panic!("Could not get OS extension, not valid UTF-8");
            }
        
        }
    }
}