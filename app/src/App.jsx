import { Fragment, useState } from 'react'
import {Home} from './pages/home';
import {Dashboard} from './pages/dashboard';
import './App.css'
import { Route, Routes } from 'react-router-dom';

function App() {
  return (
    <Fragment>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Fragment>
  )
}

export default App
