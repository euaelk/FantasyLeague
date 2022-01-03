import logo from "./logo.svg";
import "./App.css";
import React, { Component } from "react";

class App extends Component {
  state = {
    employees: [],
    isLoading: true,
  };

  async componentDidMount() {
    // fetch employee API and set response body in the employees variable
    const response = await fetch("/api/employees");
    const body = await response.json();
    this.setState({ employees: body, isLoading: false });
  }

  render() {
    const { employees, isLoading } = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <div className="App-intro">
            <h2>Employees</h2>

            {employees.map((employee) => (
              <div key={employee.id}>
                {employee.firstName} {employee.lastName}
              </div>
            ))}
          </div>
        </header>
      </div>
    );
  }
}

export default App;
