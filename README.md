# TestMAX-DLMS

This repository contains our initial setup and first mock test case for the **TestMAX** framework.  
The goal is to demonstrate how TestMAX can be used with XML-defined test cases and simple mocking, before moving to real DLMS protocol integration.

---

## ✅ What is included
- Project cloned and configured to run with **Java 17 (Amazon Corretto)** and **Apache Maven 3.9+**.  
- One mock test case defined in XML: **HelloDLMS.xml**  
  - Sets dummy DLMS values: `frame`, `OBIS`, `value`, `unit`  
  - Validates them using XML-based assertions.  
- A tiny JUnit test (`AlwaysPass.java`) to demonstrate Java integration.  
- Automatic **logs** and **performance graphs** generated for each run under `/output`.  

---

## ⚙️ Prerequisites
- Java JDK 17+ (recommended: Amazon Corretto 17)  
- Apache Maven 3.9+  
- Git

Verify installation:
```bash
java -version
mvn -version
```

---

## 🚀 How to run
1. Clone the repository:
   ```bash
   git clone https://github.com/OliverPCV/testmax-dlms.git
   cd testmax-dlms/testmax
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the mock test case:
   ```bash
   mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass="com.testmax.runner.MyRunner"
   ```

---

## 📂 Project structure & important files

### XML Test Case
- **`testmax/data/module/Template/HelloDLMS.xml`**  
  → This is the main XML test case.  
  → All TestMAX test cases are stored under `data/module/<ModuleName>/`.  
  → In our case, we used `Template/` because this is just a mock/example, not a production module.  
  → The file defines one `<page>` called `HelloDLMS` with an `<action>` named `MockFrame`.

### Custom Runner
- **`testmax/src/main/java/com/testmax/runner/MyRunner.java`**  
  → Custom Java entrypoint we wrote to directly run the `HelloDLMS` test without Jenkins or external suite manager.  
  → It fills in the 17 arguments that TestMAX expects (suite name, page, action, threads, etc.).

### JUnit Smoke Test
- **`testmax/src/main/java/com/testmax/samples/custom/AlwaysPass.java`**  
  → Minimal JUnit test that always passes (`assertTrue(true)`).  
  → Purpose: to show that Java/JUnit tests can be integrated inside the XML flow.

### Output (Logs & Graphs)
- **`testmax/output/`**  
  → After each run, TestMAX creates a timestamped folder here.  
  → Inside you’ll find:
    - `HelloSuite_<timestamp>.log` → execution log (shows each validator PASS/FAIL)  
    - PNG graphs for execution, response time, elapsed time, active users.  

---

## 📊 Example output
- **Logs:** show that the mock values (`frame`, `OBIS`, `value`, `unit`) are set and validated.  
- **Graphs:** execution, response time, elapsed time, and active users are automatically generated.


