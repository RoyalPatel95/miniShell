# 🖥️ Java Mini Shell

A custom Unix-like shell implementation built in **Java**.

This project simulates basic shell behavior including:
- Command parsing with quote handling
- Built-in commands
- External command execution
- Output redirection
- PATH resolution

---

## 🚀 Features

### 1️⃣ Command Parsing
- Supports whitespace-based token separation
- Handles:
  - Single quotes `'example text'`
  - Double quotes `"example text"`
  - Escape characters `\`
- Preserves quoted strings as single arguments

Example:
```
echo "Hello World"
echo 'Java Shell'
```

---

### 2️⃣ Built-in Commands

#### 🔹 `echo`
Prints text to the terminal.

```
echo Hello
echo "Hello World"
```

Supports output redirection:
```
echo Hello > file.txt
```

---

#### 🔹 `type`
Checks whether a command is:
- A shell built-in
- An external executable from PATH

Example:
```
type echo
type java
```

---

#### 🔹 `exit`
Exits the shell.

```
exit
```

---

### 3️⃣ Output Redirection

Supports:
```
> 
1>
```

Example:
```
echo Hello > output.txt
dir > list.txt
```

- Redirects only standard output
- Error output still prints to terminal

---

### 4️⃣ External Command Execution

- Executes system commands using `ProcessBuilder`
- Searches executable in system `PATH`
- Streams stdout and stderr properly

Examples:
```
dir
java -version
ipconfig
```

If command not found:
```
xyz
```
Output:
```
xyz: command not found
```

---

## 🛠️ Technologies Used

- Java
- ProcessBuilder API
- File I/O (FileOutputStream)
- Environment Variables (PATH)
- String Parsing Logic

---

## 📂 Project Structure

```
Main.java
README.md
```

---

## ▶️ How to Run

### 1. Compile

```
javac Main.java
```

### 2. Run

```
java Main
```

---

## 💡 Sample Session

```
$ echo Hello World
Hello World

$ type echo
echo is a shell builtin

$ java -version
(openjdk version output)

$ echo Test > file.txt

$ exit
```

---

## 📌 Limitations

- No piping (`|`) support
- No background execution (`&`)
- No environment variable expansion
- No append redirection (`>>`)

---

## 🔥 Future Improvements

- Add pipe (`|`) support
- Add append redirection (`>>`)
- Add environment variable expansion (`$HOME`)
- Add command history
- Add auto-completion

---

## 🎯 Author

Developed as a learning project to understand:
- How shells parse commands
- How operating systems execute processes
- How redirection works internally

---

## 📜 License

This project is open-source and available for educational purposes.