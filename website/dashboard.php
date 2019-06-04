<?php
    session_start();
    if (isset($_SESSION["user"])) {
        //ok
    }
    else {
        login();
    }
    function error_out($m=null) {
		if ($m == null) {
			header("Location: index.php");
            die();
        }
        else {
            header("Location: index.php?reason=$m");
            die();
        }
	}
	function success_out() {
		//die (json_encode(array('success'=>true)));
	}
    function get_param($p, $d=false) {
		return (isset($_POST[$p]) ? $_POST[$p] : $d);
	}
    function get_pdo_connection() {
		try {
			$config = parse_ini_file("opentill.ini");
			return new PDO('mysql:host=' . $config['database_host'] . ':' . $config['database_port'] . ';dbname=' . $config['database_name'], $config['database_username'], $config['database_password']);
		}
		catch(PDOException $e)
		{
			L($e->getMessage());
		}
	}
    function login() {
		$email = get_param('email', null);
		$password = get_param('password', null);
		if (($email == null) || ($password == null)) {
			error_out("missing parameters");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare('SELECT id, name FROM kvs_operators WHERE LCASE(email) = LCASE(?) AND passwordHash = ? LIMIT 1');
		$stmt->bindValue(1, $email);
		$stmt->bindValue(2, hash("sha512", $password));
		$stmt->execute();
		if ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			//L("User (" . $rs["id"] . ") " . $rs["name"] . " logged in successfully");
			$_SESSION["user"] = $rs["id"];
			success_out();
		}
		error_out("Email or password incorrect");
	}
	function logout() {
		unset($_SESSION["user"]);
		success_out();
    }
?>
<h1>Hello World</h1>