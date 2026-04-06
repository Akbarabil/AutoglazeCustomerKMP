package com.example.autoglazecustomer.data.di

import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.CabangService
import com.example.autoglazecustomer.data.network.HomeService
import com.example.autoglazecustomer.data.network.ProductService
import com.example.autoglazecustomer.data.network.TransactionService
import com.example.autoglazecustomer.data.network.VehicleService
import com.example.autoglazecustomer.ui.cart.CartScreenModel
import com.example.autoglazecustomer.ui.checkvehicle.CheckVehicleScreenModel
import com.example.autoglazecustomer.ui.home.HomeScreenModel
import com.example.autoglazecustomer.ui.login.LoginScreenModel
import com.example.autoglazecustomer.ui.password.RequestPasswordScreenModel
import com.example.autoglazecustomer.ui.profile.ProfileScreenModel
import com.example.autoglazecustomer.ui.profile.editprofile.EditProfileScreenModel
import com.example.autoglazecustomer.ui.profile.myvehicle.MyVehicleScreenModel
import com.example.autoglazecustomer.ui.profile.myvehicle.addvehicle.AddVehicleScreenModel
import com.example.autoglazecustomer.ui.profile.myvoucher.MyVoucherScreenModel
import com.example.autoglazecustomer.ui.register.RegisterScreenModel
import com.example.autoglazecustomer.ui.register.RegisterVehicleScreenModel
import com.example.autoglazecustomer.ui.register.SurveyScreenModel
import com.example.autoglazecustomer.ui.transaction.TransactionScreenModel
import com.example.autoglazecustomer.ui.transaction.VehicleSelectionScreenModel
import com.example.autoglazecustomer.ui.transaction.checkout.CheckoutScreenModel
import com.example.autoglazecustomer.ui.transaction.jasa.JasaListScreenModel
import com.example.autoglazecustomer.ui.transaction.membership.MembershipListScreenModel
import com.example.autoglazecustomer.ui.transaction.produk.ProdukListScreenModel
import com.example.autoglazecustomer.ui.transaction.voucher.VoucherScreenModel
import org.koin.dsl.module

val appModule = module {

    single { AuthService() }
    single { HomeService() }
    single { CabangService() }
    single { ProductService() }
    single { VehicleService() }
    single { TransactionService() }


    factory { LoginScreenModel(authService = get()) }
    factory { RequestPasswordScreenModel(authService = get()) }
    factory { RegisterScreenModel(authService = get()) }
    factory { RegisterVehicleScreenModel(vehicleService = get()) }
    factory {
        SurveyScreenModel(
            authService = get(),
            cabangService = get()
        )
    }
    factory {
        HomeScreenModel(
            authService = get(),
            homeService = get(),
            vehicleService = get(),
            transactionService = get(),
            cabangService = get()
        )
    }
    factory {
        CartScreenModel(
            vehicleService = get(),
            transactionService = get()
        )
    }
    factory {
        ProfileScreenModel(
            authService = get(),
            transactionService = get()
        )
    }

    factory { EditProfileScreenModel(authService = get()) }
    factory { MyVehicleScreenModel(vehicleService = get()) }
    factory { AddVehicleScreenModel(vehicleService = get()) }
    factory { CheckVehicleScreenModel(vehicleService = get()) }
    factory {
        MyVoucherScreenModel(
            vehicleService = get(),
            transactionService = get()
        )
    }
    factory { TransactionScreenModel(cabangService = get()) }
    factory { (kodeCabang: String) ->
        VehicleSelectionScreenModel(
            vehicleService = get(),
            productService = get(),
            kodeCabang = kodeCabang
        )
    }
    factory { (kodeCabang: String, idKendaraan: Int, membershipStatusInt: Int) ->
        JasaListScreenModel(
            productService = get(),
            kodeCabang = kodeCabang,
            idKendaraan = idKendaraan,
            membershipStatusInt = membershipStatusInt
        )
    }
    factory { (kodeCabang: String, membershipStatusInt: Int) ->
        ProdukListScreenModel(
            productService = get(),
            kodeCabang = kodeCabang,
            membershipStatusInt = membershipStatusInt
        )
    }
    factory { (kodeCabang: String) ->
        MembershipListScreenModel(
            productService = get(),
            kodeCabang = kodeCabang
        )
    }

    factory { (idKendaraan: Int, cartItems: List<CartItem>) ->
        VoucherScreenModel(
            transactionService = get(),
            idKendaraan = idKendaraan,
            cartItems = cartItems
        )
    }

    factory { (cabang: CabangData, vehicle: VehicleWithStatus) ->
        CheckoutScreenModel(
            transactionService = get(),
            cabang = cabang,
            vehicle = vehicle
        )
    }
}