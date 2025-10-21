import CustomHeader from '@/components/custom/CustomHeader'
import React from 'react'
import { Outlet } from 'react-router'

const layout = () => {
    return (
        <div className="min-h-screen bg-background">
            <CustomHeader></CustomHeader>
            <main className="p-0">
                <Outlet />
            </main>
        </div>
    )
}

export default layout
