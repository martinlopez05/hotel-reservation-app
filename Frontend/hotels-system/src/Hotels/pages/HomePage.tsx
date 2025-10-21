import type React from "react"
import { useState } from "react"
import { hotels } from "@/lib/data"
import CustomDescription from "@/components/custom/CustomDescription"
import HotelGrid from "../components/HotelGrid"
import Searches from "../components/Searches"
import useHotels from "../Hooks/useHotels"

export default function HomePage() {

    const { data: hotels } = useHotels();

    return (
        <div>
            {/* Header */}
            {/* Hero Section */}
            <CustomDescription></CustomDescription>
            {/* Search and Filters */}
            <Searches hotels={hotels ? hotels : []}></Searches>
            <HotelGrid hotels={hotels ? hotels : []}></HotelGrid>
        </div>
    )
}
